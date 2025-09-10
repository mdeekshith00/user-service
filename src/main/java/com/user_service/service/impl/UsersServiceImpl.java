package com.user_service.service.impl;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

import com.common.constants.CommonConstants;
import com.common.enums.StatusType;
import com.common.exception.BloodBankBusinessException;
import com.user_service.dto.JWTResponse;
import com.user_service.dto.MinUserDto;
import com.user_service.dto.RefreshTokenRequest;
import com.user_service.dto.UserDto;
import com.user_service.entities.RefreshToken;
import com.user_service.entities.Role;
import com.user_service.entities.Users;
import com.user_service.mapper.RoleMapper;
import com.user_service.repositary.RefreshTokenrepositary;
import com.user_service.repositary.RoleRepositary;
import com.user_service.repositary.UserRepositary;
import com.user_service.service.RefreshTokenService;
import com.user_service.service.UsersService;
import com.user_service.vo.UsersVo;
import com.user_service.vo.loginUservo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UsersServiceImpl implements UsersService , RefreshTokenService {
	
	private final UserRepositary userRepositary;
	private final RoleRepositary roleRepositary;
	private final JWTService jwtServcie;
	private final ModelMapper uModelMapper;
	private final AuthenticationManager authManager;
	private final RoleMapper roleMapper;
	private final RefreshTokenrepositary refreshTokenRepositary;
	
	private  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
	
	@Override
	public UserDto register(UsersVo userVo) {
		// TODO Auto-generated method stub
	   Users user = new Users();
	   log.info("user register...." );

	   
	  Set<Role> roles =  userVo.getRoles().stream().map(r -> {
			if(r.getRole() == null) {
				throw new BloodBankBusinessException(null);
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

		user = userRepositary.save(user);
		
		UserDto userDto = uModelMapper.map(user, UserDto.class);
		userDto.setStatus(CommonConstants.SUCESS);
		return userDto;
	}
	@Override
	public JWTResponse  login(loginUservo loginUservo) {
		Users user = userRepositary.findByUsername(loginUservo.getUsername());
		if(user == null) {
			throw new BloodBankBusinessException(null);
//			throw new UserDetailsNotFoundException("user details not found .." + loginUservo.getUsername());
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
		RefreshToken token =  createrefreshToken(loginUservo.getUsername());
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
			  .orElseThrow(() ->  new BloodBankBusinessException(null));
	  log.debug("retriveing user from db {} : " + userId);
	  UserDto uDto = new UserDto();
	  
	  if(!user.getIsActive()) {   
		  throw new BloodBankBusinessException(null);
		  
	  }
	  uDto.setStatus(CommonConstants.SUCESS);
	  uDto.setMessage("Succesfully login in to user");
//	  uDto = userMapper.toDto(user);
		return uDto;
	}

	@Override
	public MinUserDto updateUsers(Integer userId, UsersVo userVo) {
		// TODO Auto-generated method stub
//		CommonUtils.verifyUserId(String.valueOf(userId));
		  Users user = userRepositary.findById(userId)
				  .orElseThrow(() ->  new BloodBankBusinessException(null));
		  if(Boolean.TRUE.equals(user.getIsActive())) {
//		  user.setAddressType(userVo.getAddressType().toString());
		  user.setUpdatedAt(LocalDateTime.now());
		  user.setEMail(userVo.getEMail());
		  user.setGender(userVo.getGender().toString());
		  user.setDateOfBirth(userVo.getDateOfBirth());
		  user = userRepositary.save(user);
		  }
		  else {
			  throw new BloodBankBusinessException(null);
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
		.orElseThrow(() ->  new BloodBankBusinessException(null));
	  
	    if(Boolean.TRUE.equals(user.getIsActive()) && Boolean.TRUE.equals(user.getIsPhoneNumberVerified())) 
	    {
		
		Optional.ofNullable(user.getIsActive().equals(null)).orElse(null);
		Optional.ofNullable(user.getIsPhoneNumberVerified().equals(null)).orElse(null);
		
	    }
	    else {
	    	throw new BloodBankBusinessException(null);
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
		throw new BloodBankBusinessException(null);
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
				throw new BloodBankBusinessException(null);
//				throw new DetailsNotFoundException("Bad credetials Entered: ");
			}
			user.setPassword(encoder.encode(password));
			userRepositary.save(user);
		} else {
			throw new BloodBankBusinessException(null);
//			throw new DetailsNotFoundException(CommonConstants.USER_DETAILS_NOTFOUND_ID + username);
		}
		return "Password Updated for User " + username;
	}
	@Override
	public JWTResponse refreshToken(RefreshTokenRequest request) {
	    // Step 1: Find token
	    RefreshToken refreshToken = refreshTokenRepositary.findByToken(request.getRefreshToken())
	            .orElseThrow(() -> new BloodBankBusinessException(null));

	    // Step 2: Check expiry
	    if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
	    	refreshToken = createOrUpdateRefreshToken(refreshToken.getUser());
	        // OR regenerate: refreshToken = createrefreshToken(refreshToken.getUser().getUsername());
	    }

	    // Step 3: Generate new Access Token
	    String newAccessToken = jwtServcie.generateToken(refreshToken.getUser());

	    // Step 4: Return Response
	    return JWTResponse.builder()
	            .accesToken(newAccessToken)
	            .token(refreshToken.getToken()) // keep same refresh token if still valid
	            .build();
	}

	public RefreshToken createrefreshToken(String username) {
		RefreshToken refreshToken = RefreshToken.builder()
				         .user(userRepositary.findByUsername(username))
		                 .token((UUID.randomUUID() + username).toString())
		                 .expiryDate(Instant.now().plusSeconds(3600))
		                 .build();
		
		return refreshTokenRepositary.save(refreshToken);
		
	}

	public Optional<RefreshToken> findByToken(String token) {
		 log.info("Searching for token: {}", token);
		 return Optional.ofNullable(refreshTokenRepositary.findByToken(token))
				 .orElseThrow(() -> new BloodBankBusinessException(null));
	}
	public void deleteToken(String token) {
		  refreshTokenRepositary.findByToken(token);
	}
	public RefreshToken verifyExpiration(RefreshToken token) {
		if(token.getExpiryDate().isBefore(Instant.now())) {
			refreshTokenRepositary.delete(token);
			throw new RuntimeException(token.getToken()  + " Refresh Token Has been expired , Plase sign again :");
		
		}	
		return token;
	}

	private RefreshToken createOrUpdateRefreshToken(Users user) {
		// TODO Auto-generated method stub
		Optional<RefreshToken> existingToken = refreshTokenRepositary.findByUser(user);
		RefreshToken refreshToken ;
		if(existingToken.isPresent()) {
			refreshToken = existingToken.get();
			refreshToken.setToken(UUID.randomUUID().toString());
		    refreshToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
		} else {
			refreshToken = new RefreshToken();
			refreshToken.setUser(user);
			refreshToken.setToken(UUID.randomUUID().toString());
			refreshToken.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
		}
		return refreshTokenRepositary.save(refreshToken);
	}

}
