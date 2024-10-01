package com.example.demo.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdTokenPayload {

	private String sub;
	private String iss;
	private String azp;
	private String aud;
	private String email;
	private boolean email_verified;
	private String at_hash;
	private String name;
	private String picture;
	private String family_name;
	private String iat;
	private String exp;
}
