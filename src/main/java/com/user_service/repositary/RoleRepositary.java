package com.user_service.repositary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.entities.Role;

public interface RoleRepositary  extends JpaRepository<Role, Integer>{

}
