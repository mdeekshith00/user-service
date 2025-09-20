package com.user_service.service.impl;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.common.exception.BloodBankBusinessException;
import com.user_service.entities.Users;
import com.user_service.repositary.UserRepositary;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserPrinicipalServiceImpl  implements UserDetailsService {
	
	private final UserRepositary uUserRepositary;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			Users user = uUserRepositary.findByUsername(username);
			if(user == null)
				throw  new  BloodBankBusinessException(null);
	return new User(user.getUsername(),user.getPassword(), 
			user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getRole()))
            .toList() );

	}

}
