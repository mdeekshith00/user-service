package com.user_service.vo;

import java.time.LocalDate;
import java.util.Set;

import com.common.enums.AddressType;
import com.common.enums.GenderType;
import com.common.vo.BaseVO;
import com.common.vo.RoleVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.user_service.entities.Address;
import com.user_service.entities.FullName;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
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
public class UpdateRequestVO extends BaseVO{
	
	
	private FullName fullname;

    private GenderType gender;

	@Email(message = "Invalid email format")
	private String eMail;
	
	private AddressType addressType;
	
	@Valid
	private Address address ;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;

	private Boolean wantToDonate; 
	
	private Set<RoleVo> roles;

	
	

}
