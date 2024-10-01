package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.demo.dto.request.GoogleLoginDto;
import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.SignupRequestDTO;
import com.example.demo.dto.request.TokenEndpointDTO;
import com.example.demo.dto.response.IdTokenPayload;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.SignupResponseDTO;
import com.example.demo.dto.response.TokenEndpointResponseDTO;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.enums.RoleEnum;
import com.example.demo.enums.SocialMedia;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.UserDetailsServiceImpl;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Qualifier("userDetailServiceImpl")
	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Value("${google.aouth.client.secret}")
	private String googleOauthClientSecret;

	@Value("${google.aouth.token.endpoint.base.url}")
	private String googleOauthTokenEndpointBaseUrl;

	@Value("${google.oauth.client.id}")
	private String googleOauthClientId;

	@Value("${google.oauth.redirect.url}")
	private String googleOauthRedirectUrl;

	@Override
	public SignupResponseDTO saveUser(SignupRequestDTO signupDto, SocialMedia socialMedia, String sub) {

		UserEntity userEntity = new UserEntity();
		userEntity.setEmail(signupDto.getEmail());
		userEntity.setName(signupDto.getName());
		if (signupDto.getPassword() != null) {
			userEntity.setPassword(passwordEncoder.encode(signupDto.getPassword()));
		}
		if(socialMedia != null) {
			userEntity.setSocialMediaLogin(socialMedia);
		}
		
		if(socialMedia != null && sub != null) {
			userEntity.setSub(sub);
		}

		List<RoleEntity> rolesToBeSaved = signupDto.getRole().stream().map(roleEnum -> {
			RoleEntity role = new RoleEntity();
			role.setId(roleEnum.getId());
			role.setName(roleEnum.toString());
			return role;
		}).toList();

		userEntity.setRole(rolesToBeSaved);

		UserEntity user = userRepository.save(userEntity);
		SignupResponseDTO responeDto = new SignupResponseDTO();
		responeDto.setUserId(user.getId());
		responeDto.setMessage("Signup Successfull");
		return responeDto;

	}

	@Override
	public LoginResponseDTO login(LoginRequestDTO loginDto) throws BadRequestException {
		Authentication authentication;
		try {
			/*
			 * check if Non-Social Media Account Exists in DB or not
			 */
		Optional<UserEntity>  existingUser=	userRepository.findByEmailAndSocialMediaLogin(loginDto.getEmail(),null);
		if(existingUser.isEmpty()) {
			throw new AuthenticationCredentialsNotFoundException("Account Does not Exist");
		}
		
		authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
		} catch (AuthenticationException exception) {
			throw new AuthenticationCredentialsNotFoundException("Incorrect Username or Passowrd");
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		String jwtToken = jwtUtil.generateTokenFromUsername(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		LoginResponseDTO response = new LoginResponseDTO("Login Successfull", jwtToken);

		return response;

	}

	@Override
	public LoginResponseDTO loginWithGoogle(GoogleLoginDto googleLoginDto) throws AuthenticationException {
		LoginResponseDTO response = new LoginResponseDTO();
		StringBuilder googleOauthTokenUrl = new StringBuilder();

		RestTemplate restTemplate = new RestTemplate();
		TokenEndpointDTO tokenEndpointDto = new TokenEndpointDTO();
		tokenEndpointDto.setClient_id(googleOauthClientId);
		tokenEndpointDto.setClient_secret(googleOauthClientSecret);
		tokenEndpointDto.setCode(googleLoginDto.getTempGrant());
		tokenEndpointDto.setRedirect_uri(googleOauthRedirectUrl);
		HttpEntity<TokenEndpointDTO> request = new HttpEntity<>(tokenEndpointDto);

		TokenEndpointResponseDTO tokenEndPointResponse = restTemplate.postForObject(googleOauthTokenEndpointBaseUrl,
				tokenEndpointDto, TokenEndpointResponseDTO.class);
		if (tokenEndPointResponse == null || tokenEndPointResponse.getId_token() == null) {
			throw new AuthenticationServiceException("google authentication failed");
		}

		ObjectMapper mapper = new ObjectMapper();

		try {
			String idToken = tokenEndPointResponse.getId_token();
			/*
			 * -id token is A JWT token consists of 3 parts seperated by dot ("."). 1)
			 * header, 2) payload and 3) signature -we need payload which is the 2nd part of
			 * JWT token and is base64 encoded
			 */
			String[] idTokenToArray = idToken.split("\\.");
			if (idTokenToArray == null || idTokenToArray.length < 3) {
				throw new AuthenticationServiceException("invalid google id token");
			}

			/*
			 * create an instance of Base64.Decoder to decode the idToken payload
			 */
			Base64.Decoder decoder = Base64.getUrlDecoder();

			String idTokenPayloadJsonString = new String(decoder.decode(idTokenToArray[1]));

			/*
			 * converting payload JSON string to Java Object
			 */
			IdTokenPayload idTokenPayload = mapper.readValue(idTokenPayloadJsonString, IdTokenPayload.class);

			/*
			 * if google email is not verified then throw exception, this condition will not
			 * be true in 99.99% case but just to be on safer side we will check this
			 */

			if (idTokenPayload.isEmail_verified() == false) {
				throw new AuthenticationServiceException("google email not verified");
			}

			/*
			 * -check if current user already exists in Database or it is first time login
			 * -if it is first time login we will save record in our Database, then generate
			 * jwt token and return -if user already saved in DB we will generate token and
			 * return
			 */
			Optional<UserEntity> user = userRepository.findByEmail(idTokenPayload.getEmail());
			if (user.isPresent()) {

				response = new LoginResponseDTO("Login Successfull", generateJwtToken(user.get().getEmail()));
			} else {
				SignupRequestDTO signupDto = new SignupRequestDTO();
				signupDto.setEmail(idTokenPayload.getEmail());
				signupDto.setName(idTokenPayload.getName());
				List<RoleEnum> roleList = new ArrayList<>();
				roleList.add(RoleEnum.USER);
				signupDto.setRole(roleList);

				saveUser(signupDto,SocialMedia.GOOGLE,idTokenPayload.getSub());
				response = new LoginResponseDTO("Login Successfull", generateJwtToken(signupDto.getEmail()));
				
			}

		} catch (Exception e) {
			throw new AuthenticationServiceException("google authentication failed -" + e.getMessage());
		}

		return response;

	}

	private String generateJwtToken(String emailId) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(emailId);
		String jwtToken = jwtUtil.generateTokenFromUsername(userDetails);
		return jwtToken;
	}

}
