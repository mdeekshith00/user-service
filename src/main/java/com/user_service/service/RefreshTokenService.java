package com.user_service.service;

import java.util.Optional;

import com.user_service.entities.RefreshToken;
import com.user_service.entities.Users;

public interface RefreshTokenService {
	
	RefreshToken createrefreshToken(Users user);
	Optional<RefreshToken> findByToken(String token);
	void deleteToken(String token);
	RefreshToken verifyExpiration(RefreshToken token);

}
