package com.user_service.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.user_service.dto.JWTResponse;
import com.user_service.dto.MinUserDto;
import com.user_service.dto.RefreshTokenRequest;
import com.user_service.dto.UserDto;
import com.user_service.entities.RefreshToken;
import com.user_service.entities.Role;
import com.user_service.entities.Users;
import com.user_service.enums.StatusType;
import com.user_service.exception.DetailsNotFoundException;
import com.user_service.exception.UserDetailsNotFoundException;
import com.user_service.mapper.RoleMapper;
import com.user_service.mapper.UserMapper;
import com.user_service.repositary.RefreshTokenrepositary;
import com.user_service.repositary.RoleRepositary;
import com.user_service.repositary.UserRepositary;
import com.user_service.service.UsersService;
import com.user_service.util.CommonConstants;
import com.user_service.vo.UsersVo;
import com.user_service.vo.loginUservo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsersServiceImpl implements UsersService {
	
	private final UserRepositary userRepositary;
	private final RoleRepositary roleRepositary;
	private final JWTService jwtServcie;
	private final ModelMapper uModelMapper;
	private final AuthenticationManager authManager;
	private final UserMapper userMapper;
	private final RoleMapper roleMapper;
	private final RefreshTokenServiceImpl refreshTokenServiceImpl;
//	private final RefreshTokenrepositary refreshTokenrepositary;
	
	private  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
	
	@Override
	public UserDto register(UsersVo userVo) {
		// TODO Auto-generated method stub
	   Users user = new Users();
	   log.info("user register...." );
//      Role role = new Role();
	   
	  Set<Role> roles =  userVo.getRoles().stream().map(r -> {
			if(r.getRole() == null) {
				throw new DetailsNotFoundException("Role name not Found : ");
			}
				Role role = new Role();
				role = Role.builder()
						.role(r.getRole().name())
						.description(r.getDescription()).build();
				roleRepositary.save(role);
				return role;
		
		}
				).collect(Collectors.toSet());
				

		 user = Users.builder()
				.fullName(userVo.getFullname())
				.username(userVo.getUsername())
				.password(encoder.encode(userVo.getPassword()))
				.eMail(userVo.getEMail())
				.phoneNumber(userVo.getPhoneNumber())
				.gender(userVo.getGender().toString())
				.addressType(userVo.getAddressType().toString())
				.address(userVo.getAddress())
                .dateOfBirth(userVo.getDateOfBirth())
                .createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.activeStatus(StatusType.IN_ACTIVE.name())
				.bio(userVo.getBio())
				.roles(roles)
				.build();
		 
//          userMapper.toEntity(userVo); 
		user = userRepositary.save(user);

//		UserDto userDto = userMapper.toDto(user);
		UserDto userDto = uModelMapper.map(user, UserDto.class);
		userDto.setStatus(CommonConstants.SUCESS);
		return userDto;
	}
	@Override
	public JWTResponse  login(loginUservo loginUservo) {
		Users user = userRepositary.findByUsername(loginUservo.getUsername());
		if(user == null) {
			throw new UserDetailsNotFoundException("user details not found Exception..");
		}
		user.setIsActive(Boolean.TRUE);
		user.setLastLogin(Timestamp.from(Instant.now()));
		user.setIsPhoneNumberVerified(Boolean.TRUE);
        user.setLoginCount(Optional.ofNullable(user.getLoginCount())
        		.map(count -> count+1).orElse((long) 1) );
        user.setActiveStatus(StatusType.ACTIVE.name());
        
		userRepositary.save(user);
		log.info("User in DB: " + user.getUsername());
		
		log.info("Password in DB: " + user.getPassword());
		log.info("Input password: " + loginUservo.getPassword());
		log.info("Matches? " + encoder.matches(loginUservo.getPassword(), user.getPassword()));
		
		Authentication authentication  = 
				authManager.authenticate(new UsernamePasswordAuthenticationToken(loginUservo.getUsername(), loginUservo.getPassword()));
		RefreshToken token =  refreshTokenServiceImpl.createrefreshToken(loginUservo.getUsername());
          String jwt = null;
		  if(authentication.isAuthenticated())
              jwt =   jwtServcie.generateToken(user);
		  
           return  JWTResponse.builder()
                                  .accesToken(jwt)
                                  .token(token.getToken())
                                  .build();
	}

	@Override
	public UserDto getUsersById(Integer userId) {
		// TODO Auto-generated method stub
//		CommonUtils.verifyUserId(String.valueOf(userId));
		log.debug("user id verified {}:" + userId);
	  Users user = userRepositary.findById(userId)
			  .orElseThrow(() ->  new UserDetailsNotFoundException(CommonConstants.USER_DATA_NOTFOUND_WITH_GIVEN_ID + userId));
	  log.debug("retriveing user from db {} : " + userId);
	  UserDto uDto = new UserDto();
	  
	  if(!user.getIsActive()) {   
		  throw new UserDetailsNotFoundException(CommonConstants.USER_NOT_IN_ACTIVE + CommonConstants.UPDATE_THE_STATUS);
		  
	  }
	  uDto.setStatus(CommonConstants.SUCESS);
	  uDto.setMessage("Succesfully login in to user");
	  uDto = userMapper.toDto(user);
		return uDto;
	}

	@Override
	public MinUserDto updateUsers(Integer userId, UsersVo userVo) {
		// TODO Auto-generated method stub
//		CommonUtils.verifyUserId(String.valueOf(userId));
		  Users user = userRepositary.findById(userId)
				  .orElseThrow(() ->  new UserDetailsNotFoundException(CommonConstants.USER_DATA_NOTFOUND_WITH_GIVEN_ID+ userId) );
		  if(Boolean.TRUE.equals(user.getIsActive())) {
//		  user.setAddressType(userVo.getAddressType().toString());
		  user.setUpdatedAt(LocalDateTime.now());
		  user.setEMail(userVo.getEMail());
		  user.setGender(userVo.getGender().toString());
		  user.setDateOfBirth(userVo.getDateOfBirth());
		  user = userRepositary.save(user);
		  }
		  else {
			  throw new UserDetailsNotFoundException(CommonConstants.USER_NOT_IN_ACTIVE + CommonConstants.UPDATE_THE_STATUS);
		  } 
		  MinUserDto minUserDto =   uModelMapper.map(user, MinUserDto.class);
		return minUserDto;
	}

	@Override
//	@CacheEvict(value = "users", key = "#userId")
	public String deleteUser(Integer userId) {
		// TODO Auto-generated method stub
//		CommonUtils.verifyUserId(String.valueOf(userId));
	  Users user = 	userRepositary.findById(userId)
		.orElseThrow(() ->  new UserDetailsNotFoundException(CommonConstants.USER_DATA_NOTFOUND_WITH_GIVEN_ID+ userId) );
	  
	    if(Boolean.TRUE.equals(user.getIsActive()) && Boolean.TRUE.equals(user.getIsPhoneNumberVerified())) 
	    {
		
		Optional.ofNullable(user.getIsActive().equals(null)).orElse(null);
		Optional.ofNullable(user.getIsPhoneNumberVerified().equals(null)).orElse(null);
		
	    }
	    else {
	    	throw new DetailsNotFoundException(CommonConstants.USER_NOT_THERE_TO_DELETE + user.getUsername());
	    }
		log.info("delted user .." + userId);
	
		return "User deleted on this Id: " + user.getUserId() + " on this Username " + user.getUsername();
	}

	@Override
//	@Cacheable(value = "Users" )
	public List<UserDto>  getAllUsers() {
		// TODO Auto-generated method stub
//		Utils.VerifyuserId(userId);
	  List<Users> users = 	userRepositary.findAll();
	  
	          users
	          .parallelStream()
	          .filter(user -> user.getIsActive().equals(Boolean.TRUE))
	          .filter(user -> user.isAccountNonExpired())
	          .filter(user -> user.getIsPhoneNumberVerified())
	          ;
	   List<UserDto> dto =    users.stream().map(user -> uModelMapper.map(user, UserDto.class)).collect(Collectors.toList());
		return dto;
	}
	
	@Override
	public String forgotPassword(String username) {
	Users user = 	userRepositary.findByUsername(username);
//	Utils.VerifyuserId(user.getUserId());
	String resetPassword = null ;
	if(user != null && user.getIsActive().equals(Boolean.TRUE) && user.getIsPhoneNumberVerified().equals(Boolean.TRUE)) {
		 resetPassword = user.getPhoneNumber() + UUID.randomUUID()+Instant.now().toString();
		user.setResetToken(resetPassword);
		userRepositary.save(user);
	} else {
		throw new DetailsNotFoundException("User Details Not Found on this Username :" + username);
	}
		return "reset your password with : " + resetPassword;
	}
	
	@Override
	public String resetPassword(String username , String resetPassword , String password) {
		Users user = 	userRepositary.findByUsername(username);
//		Utils.VerifyuserId(user.getUserId());
		if(user != null && user.getIsActive().equals(Boolean.TRUE) && user.getIsPhoneNumberVerified().equals(Boolean.TRUE)) {
			if(user.getResetToken().equalsIgnoreCase(resetPassword)) {
		                     	user.setResetToken(null);
			} else {
				throw new DetailsNotFoundException("Bad credetials Entered: ");
			}
			user.setPassword(encoder.encode(password));
			userRepositary.save(user);
		} else {
			throw new DetailsNotFoundException(CommonConstants.USER_DETAILS_NOTFOUND_ID + username);
		}
		return "Password Updated for User " + username;
	}
	@Override
	public JWTResponse refreshToken(RefreshTokenRequest request) {
		JWTResponse jwtResponse = null ;
		Optional<RefreshToken> token = refreshTokenServiceImpl.findByToken(request.getRefreshToken());
		 RefreshToken refreshToken;
		if(token.isPresent()) {
		  boolean date = token.get().getExpiryDate().isBefore(Instant.now());
			if(date) {
				refreshToken = refreshTokenServiceImpl.createrefreshToken(token.get().getUser().getUsername());
				jwtResponse = JWTResponse.builder()
						.accesToken(jwtServcie.generateToken(token.get().getUser()))
						.token(refreshToken.getToken())
						.build();
			} 
		} else {
			refreshToken = refreshTokenServiceImpl.createrefreshToken(token.get().getUser().getUsername());
		}
		
		
		return jwtResponse;
	}

}
