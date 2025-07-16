package com.user_service.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class AddressVo {
	
	private String line1;
	// Flat, street, etc.
	 private String city;
	 
    private String state;
    
	 private String country;
	 
	 private String pincode;

}
