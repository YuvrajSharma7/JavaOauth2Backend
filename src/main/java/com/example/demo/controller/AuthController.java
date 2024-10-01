package com.example.demo.controller;

import java.net.URI;

import javax.security.sasl.AuthenticationException;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.request.GoogleLoginDto;
import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.SignupRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.SignupResponseDTO;
import com.example.demo.dto.response.SocialMediaLoginResponse;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthService authService;

	@Value("${google.oauth.base.url}")
	private String googleOauthBaseUrl;

	@Value("${google.oauth.redirect.url}")
	private String googleOauthRedirectUrl;

	@Value("${google.oauth.response.type}")
	private String googleOauthResponseType;

	@Value("${google.oauth.client.id}")
	private String googleOauthClientId;

	@Value("${google.oauth.scope}")
	private String googleOauthScope;

	@Value("${google.oauth.access.type}")
	private String googleOauthAccesstype;

	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestBody SignupRequestDTO dto) {
		SignupResponseDTO responeDto = authService.saveUser(dto,null,null);
		return ResponseEntity.ok(responeDto);
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto)
			throws AuthenticationException, BadRequestException {
		LoginResponseDTO responeDto = null;
		try {
			responeDto = authService.login(dto);
		} catch (AuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;

		}
		return ResponseEntity.ok(responeDto);
	}

	@GetMapping("/redirect/google-login")
	public ResponseEntity<SocialMediaLoginResponse> redirectToGoogleLogin()
			throws AuthenticationException, BadRequestException {
		SocialMediaLoginResponse responsePayload = new SocialMediaLoginResponse();
		StringBuilder googleOauthUrl = new StringBuilder();
		googleOauthUrl.append(googleOauthBaseUrl).append("?redirect_uri=").append(googleOauthRedirectUrl)
				.append("&response_type=").append(googleOauthResponseType).append("&client_id=")
				.append(googleOauthClientId).append("&scope=").append(googleOauthScope).append("&access_type=")
				.append(googleOauthAccesstype);
		responsePayload.setUrl(googleOauthUrl.toString());

		return ResponseEntity.ok(responsePayload);
	}

	@PostMapping("/login/google")
	public ResponseEntity<LoginResponseDTO> loginWithGoogle(@RequestBody GoogleLoginDto googleLoginDto)
			throws AuthenticationException, BadRequestException {
		LoginResponseDTO tokenDto = this.authService.loginWithGoogle(googleLoginDto);
		return ResponseEntity.ok(tokenDto);
	}
}
