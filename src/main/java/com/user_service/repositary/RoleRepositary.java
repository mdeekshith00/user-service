package com.user_service.repositary;

<<<<<<< HEAD
import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.entities.Role;

public interface RoleRepositary  extends JpaRepository<Role, Integer>{
=======
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user_service.entities.Role;
import com.user_service.entities.Users;

@Repository
public interface RoleRepositary  extends JpaRepository<Role, Integer>{
	
	Optional<Role> findByUsers(Users user);

	Role findByRole(String name);

	Optional<Role> findByRoleIgnoreCase(String name);
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50

}
