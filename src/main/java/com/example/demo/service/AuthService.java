package com.example.demo.service;

import javax.security.sasl.AuthenticationException;

import org.apache.coyote.BadRequestException;

import com.example.demo.dto.request.GoogleLoginDto;
import com.example.demo.dto.request.LoginRequestDTO;
import com.example.demo.dto.request.SignupRequestDTO;
import com.example.demo.dto.response.LoginResponseDTO;
import com.example.demo.dto.response.SignupResponseDTO;
import com.example.demo.enums.SocialMedia;

public interface AuthService {

	SignupResponseDTO saveUser(SignupRequestDTO signupDto,SocialMedia socialMedia, String sub);
	
	LoginResponseDTO login(LoginRequestDTO loginDto) throws AuthenticationException, BadRequestException;
	
	LoginResponseDTO loginWithGoogle(GoogleLoginDto googleLoginDto) throws AuthenticationException;
}
