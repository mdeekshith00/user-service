package com.user_service.repositary;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user_service.entities.RefreshToken;

@Repository
public interface RefreshTokenrepositary  extends JpaRepository<RefreshToken, Integer>{

	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByUserId(Integer userId);

}
