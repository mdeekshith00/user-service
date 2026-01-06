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
public class UserPrinicipalServiceImpl implements UserDetailsService {

    private final UserRepositary userRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Users user = userRepository.findByUsername(username);

        if (user == null) {
            throw new DetailsNotFoundException(
                    "Username not found: " + username
            );
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
