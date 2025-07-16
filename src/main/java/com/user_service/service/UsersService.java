package com.user_service.service;

import com.user_service.entities.Users;
import com.user_service.vo.UsersVo;

public interface UsersService {
	Users addUsers(UsersVo userVo);
	Users getUsersById(Integer uId);
	Users updateUsers(Integer uId, UsersVo userVo);
	void deleteUsers(Integer uId);
	Users getAllUsers();

}
 