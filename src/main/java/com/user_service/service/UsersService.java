<<<<<<< HEAD
package com.user_service.service;

import com.user_service.entities.Users;
import com.user_service.vo.UsersVo;

public interface UsersService {
	Users addUsers(UsersVo userVo);
	Users getUsersById(Integer uId);
	Users updateUsers(Integer uId, UsersVo userVo);
	void deleteUsers(Integer uId);
	Users getAllUsers();
=======

package com.user_service.service;

import java.util.List;

import com.common.dto.DonorResponseDto;
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
	DonorResponseDto getDonorRole(Integer userId);
		
//   Page<SearchDto> getPaginatedUsersandBloodGroup(int page, int size, String bloodGroup);
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50

}
 