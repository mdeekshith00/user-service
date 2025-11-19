package com.user_service.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.common.enums.StatusType;
import com.user_service.dto.UserDto;
import com.user_service.entities.Users;

@Component
public class MapperHelper {
	
	public UserDto userToDto(Users user) {
		// TODO Auto-generated method stub
	return   UserDto.builder()
			.fullname(user.getFullName().toString())
				 .eMail(user.getEMail())
				 .gender(user.getGender())
				 .dateOfBirth(user.getDateOfBirth())
				 .addressType(user.getAddressType())
				 .wantToDonate(user.getWantToDonate())
				 .build();
		
	}

	

}
