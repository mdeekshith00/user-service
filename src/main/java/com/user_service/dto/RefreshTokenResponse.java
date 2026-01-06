package com.user_service.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshTokenResponse {
	
	private String accesToken;
	
	private String refreshToken;

}
