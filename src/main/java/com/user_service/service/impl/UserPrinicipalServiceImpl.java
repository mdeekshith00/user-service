package com.user_service.service.impl;

<<<<<<< HEAD
import java.util.List;

=======
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

<<<<<<< HEAD
import com.user_service.entities.Users;
import com.user_service.exception.DetailsNotFoundException;
import com.user_service.repositary.UserRepositary;

=======
import com.common.exception.BloodBankBusinessException;
import com.user_service.entities.Users;
import com.user_service.repositary.UserRepositary;
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
<<<<<<< HEAD
public class UserPrinicipalServiceImpl  implements UserDetailsService{
=======
public class UserPrinicipalServiceImpl  implements UserDetailsService {
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
	
	private final UserRepositary uUserRepositary;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			Users user = uUserRepositary.findByUsername(username);
			if(user == null)
<<<<<<< HEAD
				throw  new  DetailsNotFoundException("UserName Not Found :" + user.getUsername());
	return new User(user.getUsername(),user.getPassword(),List.of(new SimpleGrantedAuthority("Admin")));

//			return user;
		
=======
				throw  new  BloodBankBusinessException(null);
	return new User(user.getUsername(),user.getPassword(), 
			user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getRole()))
            .toList() );
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50

	}

}
