package com.user_service.service;

import java.util.List;

import com.user_service.dto.JWTResponse;
import com.user_service.dto.RefreshTokenRequest;
import com.user_service.dto.UserDto;
import com.user_service.vo.UpdateRequestVO;
import com.user_service.vo.UsersVo;
import com.user_service.vo.loginUservo;

public interface UsersService {
	
	UserDto register(UsersVo userVo);
	JWTResponse  login(loginUservo loginUservo);
	UserDto getUsersById(Integer userId);
	UserDto updateUsers(Integer userId, UpdateRequestVO updateRequestVO);
	String deleteUser(Integer userId);
	List<UserDto> getAllUsers();
	String forgotPassword(String username);
	String resetPassword(String username , String resetPassword , String password);
	JWTResponse refreshToken(RefreshTokenRequest refreshToken);
		
//   Page<SearchDto> getPaginatedUsersandBloodGroup(int page, int size, String bloodGroup);

}
 