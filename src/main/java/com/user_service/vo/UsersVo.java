package com.user_service.vo;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.user_service.entities.Address;
import com.user_service.entities.Role;

import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsersVo {
	
	private FullNameVo fullname;
	
	private String username;
	
	private String password;
	
	private String phoneNumber;
	
    private Boolean isPhoneNumberVerified;

    private String bloodGroup;
    
	private String gender;

	private String eMail;

	private AddressVo address ;

	private Boolean isAvailableToDonate;
	
	private LocalDate dateOfBirth;
	
	private Boolean isActive;
	
	private Long loginCount;
	
	private Timestamp lastLogin;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
	
	private LocalDateTime lastDonationDate;
	
	private String resetToken;
	
	private String bio;
	
	private String logInProvider;
	
	private Set<RoleVo> roles;

}
