package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenEndpointResponseDTO {

	private String access_token;
	private String expires_in;
	private String id_token;
	private String scope;
	private String token_type;
	private String refresh_token;
}
