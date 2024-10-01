package com.example.demo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TokenEndpointDTO {
private String code;
private String client_id;
private String client_secret;
private String redirect_uri;
private String grant_type = "authorization_code";
}
