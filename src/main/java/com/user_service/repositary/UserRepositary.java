package com.user_service.repositary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.entities.Users;

public interface UserRepositary extends JpaRepository<Users, Integer>{

	Users findByUsername(String username);

}
