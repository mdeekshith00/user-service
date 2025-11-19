package com.user_service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.common.dto.BaseDTO;
import com.common.vo.AddressVo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.user_service.vo.FullNameVo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchDto extends BaseDTO {
	
	  private FullNameVo fullname;
		
		private String username;
			
		private String phoneNumber;
		
	    private Boolean isPhoneNumberVerified;

	    private String bloodGroup;
	    
		private String gender;

		private String eMail;

		private AddressVo address ;

		private Boolean isAvailableToDonate;
		
//		private AddressType addressType;
		
		private LocalDate dateOfBirth;
		
		private Boolean isActive;

		private LocalDateTime updatedAt;

		private LocalDateTime lastDonationDate;

		
		


}
