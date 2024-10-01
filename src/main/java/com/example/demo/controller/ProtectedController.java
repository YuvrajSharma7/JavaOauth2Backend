package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.response.ProfileResponseDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.security.UserDetailsImpl;

@RestController
@RequestMapping("/api")
public class ProtectedController {

	
	
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
	

	
	
	
	@GetMapping("/profile")
	public ResponseEntity<?> testRedirest() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		//get current user details
		UserDetailsImpl currentUserDetails = (UserDetailsImpl) auth.getPrincipal();
		
        //prepare dto and return response
		ProfileResponseDTO profile = new ProfileResponseDTO();
		profile.setEmail(currentUserDetails.getEmail());
		profile.setName(currentUserDetails.getName());
		return ResponseEntity.ok(profile);
	}
	
}
