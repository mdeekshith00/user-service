package com.user_service.service.impl;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.user_service.entities.Users;
import com.user_service.exception.DetailsNotFoundException;
import com.user_service.repositary.UserRepositary;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserPrinicipalServiceImpl  implements UserDetailsService{
	
	private final UserRepositary uUserRepositary;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			Users user = uUserRepositary.findByUsername(username);
			if(user == null)
				throw  new  DetailsNotFoundException("UserName Not Found :" + user.getUsername());
	return new User(user.getUsername(),user.getPassword(),List.of(new SimpleGrantedAuthority("Admin")));

//			return user;
		

	}

}
