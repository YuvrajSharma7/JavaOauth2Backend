package com.example.demo.dto.request;

import java.util.List;

import com.example.demo.enums.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequestDTO {

	private String name;

	private String email;
  
	private String password;
	
	private List<RoleEnum> role;
}
