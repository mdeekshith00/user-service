package com.user_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.common.dto.BaseDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.user_service.vo.AddressVo;
import com.user_service.vo.FullNameVo;
import com.user_service.vo.RoleVo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MinUserDto extends BaseDTO{
	
	
    private FullNameVo fullname;
	
	private String username;

	private String phoneNumber;
	
    private Boolean isPhoneNumberVerified;

    private String bloodGroup;
    
	private String gender;

	private String eMail;

	private AddressVo address ;

	private Boolean isAvailableToDonate;
	
	private LocalDate dateOfBirth;
	
	private Boolean isActive;

	private LocalDateTime updatedAt;
	
	private LocalDateTime lastDonationDate;
	
	private String bio;
		
	private Set<RoleVo> roles;

}
