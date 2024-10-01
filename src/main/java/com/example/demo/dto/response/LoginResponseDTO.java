package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO extends ResponseDTO {
private String token;

public LoginResponseDTO(String message,String token) {
	super(message);
	this.token = token;
}
}
