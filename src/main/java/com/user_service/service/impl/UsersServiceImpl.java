package com.user_service.service.impl;

import org.springframework.stereotype.Service;

import com.user_service.entities.Users;
import com.user_service.repositary.UserRepositary;
import com.user_service.service.UsersService;
import com.user_service.vo.UsersVo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersServiceImpl implements    UsersService {
	
	private final UserRepositary userRepositary;
	

	@Override
	public Users addUsers(UsersVo userVo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Users getUsersById(Integer uId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Users updateUsers(Integer uId, UsersVo userVo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUsers(Integer uId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Users getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
