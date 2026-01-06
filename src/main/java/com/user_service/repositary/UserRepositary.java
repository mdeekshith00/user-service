package com.user_service.repositary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.user_service.entities.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.user_service.entities.Users;


@Repository
public interface UserRepositary extends JpaRepository<Users, Integer>{

     @EntityGraph(attributePaths = {"roles"})
	Users findByUsername(String username);
     
     @EntityGraph(attributePaths = {"roles"})
	Optional<Users> findByUserIdAndIsActiveAndIsPhoneNumberVerified(Integer userId, boolean isActive , boolean isPhoneNumberVerified);
	Optional<Users> findByPhoneNumber(String mobileNumber);
	Optional<Users> findByUsernameAndPhoneNumber(String username , String phoneNumber);
	Optional<Users> findByUserIdAndIsActive(Integer userId, boolean isActive);

	 List<Users> findByWantToDonateTrue();
	 Page<Users> findByWantToDonateTrue(Pageable pageable);
	 // Filter by blood group + location
//	  List<Users> findByWantToDonateTrueAndBloodGroupAndAddressContainingIgnoreCase(
//	            String bloodGroup, String location);

	    // With pagination
//	  Page<Users> findByWantToDonateTrueAndBloodGroupAndAddressContainingIgnoreCase(
//	            String bloodGroup, String location, Pageable pageable);
}
