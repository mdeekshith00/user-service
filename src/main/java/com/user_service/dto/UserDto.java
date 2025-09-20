package com.user_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.common.dto.BaseDTO;
import com.common.enums.AddressType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.user_service.vo.AddressVo;
import com.user_service.vo.FullNameVo;
import com.user_service.vo.RoleVo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto extends BaseDTO {
	
    private String fullname;
	
	private String username;
		
	private String phoneNumber;
	
    private Boolean isPhoneNumberVerified;
    
	private String gender;

	private String eMail;

	private AddressVo address ;

//	private Boolean isAvailableToDonate;

	private String addressType;
	
	private LocalDate dateOfBirth;
	
	private Boolean isActive;
	
	private LocalDateTime updatedAt;
	
//	private LocalDateTime lastDonationDate;
		
	private String bio;
	
	private Boolean wantToDonate; 
	
	private Set<RoleVo> roles;


}
