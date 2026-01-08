package com.user_service.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.common.constants.ErrorConstants;
import com.common.exception.BloodBankBusinessException;
import com.user_service.entities.Users;
import com.user_service.repositary.UserRepositary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserPrinicipalServiceImpl implements UserDetailsService {

    private final UserRepositary userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Users user = userRepository.findByUsername(username);

        if (user == null) {
        	 throw new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND, HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);
        }

        return new User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role ->
                                new SimpleGrantedAuthority(role.getRole()))
                        .toList()
        );
    }
}
