package com.user_service.repositary;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user_service.entities.Users;


@Repository
public interface UserRepositary extends JpaRepository<Users, Integer>{

	Users findByUsername(String username);
	Optional<Users> findByUserIdAndIsActive(Integer userId, Boolean isActive);
	Optional<Users> findByUsernameAndPhoneNumber(String username , String phoneNumber);

}
