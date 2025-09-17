package com.user_service.mapper;

import com.user_service.dto.UserDto;
import com.user_service.entities.Users;


public class MapperHelper {
	
	public static UserDto userToDto(Users user) {
		if(user == null) {
			return null;
		}
		// TODO Auto-generated method stub
	return   UserDto.builder()
				 .eMail(user.getEMail())
				 .wantToDonate(user.getWantToDonate())
				 .gender(user.getGender())
				 .dateOfBirth(user.getDateOfBirth())
				 .addressType(user.getAddressType())
				 .build();
		
	}
	

}
