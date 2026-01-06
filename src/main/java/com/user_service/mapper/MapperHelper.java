package com.user_service.mapper;

import org.springframework.stereotype.Component;

import com.common.dto.SearchDonorDTO;
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
	public  SearchDonorDTO userToSearchDonorDTO(Users user) {

	    SearchDonorDTO dto = new SearchDonorDTO();
	    dto.setUserId(user.getUserId());
//	    dto.setFullName(user.getFullName());
//	    dto.setBloodGroup(user.getBloodGroup());
//	    dto.setLocation(user.getAddress());
	    dto.setPhoneNumber(user.getPhoneNumber());
	    dto.setStatus("WANT_TO_DONATE"); // for UI distinction

	    return dto;
	}


	

}
