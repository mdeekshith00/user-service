package com.user_service.service.impl;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.common.constants.CommonConstants;
import com.common.constants.ErrorConstants;
import com.common.dto.DonorResponseDto;
import com.common.enums.RoleType;
import com.common.enums.StatusType;
import com.common.exception.BloodBankBusinessException;
import com.common.vo.RoleVo;
import com.user_service.dto.JWTResponse;
import com.user_service.dto.RefreshTokenRequest;
import com.user_service.dto.UserDto;
import com.user_service.entities.RefreshToken;
import com.user_service.entities.Role;
import com.user_service.entities.Users;
import com.user_service.mapper.MapperHelper;
import com.user_service.repositary.RefreshTokenrepositary;
import com.user_service.repositary.RoleRepositary;
import com.user_service.repositary.UserRepositary;
import com.user_service.service.RefreshTokenService;
import com.user_service.service.UsersService;
import com.user_service.vo.UpdateRequestVO;
import com.user_service.vo.UsersVo;
import com.user_service.vo.loginUservo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService , RefreshTokenService {
	
	private final UserRepositary userRepositary;
	private final RoleRepositary roleRepositary;
	private final UserNotificationService userNotificationService;
	private final JWTService jwtServcie;
	private final ModelMapper uModelMapper;
	private final AuthenticationManager authManager;
	private final RefreshTokenrepositary refreshTokenRepositary;
	private final MapperHelper mapperHelper;
	
	private  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
	
	@Override
	public UserDto register(UsersVo userVo) {
		// TODO Auto-generated method stub
	   Users user = new Users();
	   log.info("user register...." );
	   
	   if (userVo.getUsername() == null || userVo.getUsername().isEmpty()) {
	        throw new BloodBankBusinessException(ErrorConstants.INVALID_DATA, HttpStatus.BAD_REQUEST, "Username is required");
	    }

	    if (userVo.getPassword() == null || userVo.getPassword().isEmpty()) {
	        throw new BloodBankBusinessException(ErrorConstants.INVALID_DATA, HttpStatus.BAD_REQUEST, "Password is required");
	    }

	    if (userVo.getRoles() == null || userVo.getRoles().isEmpty()) {
	        throw new BloodBankBusinessException(ErrorConstants.ROLE_NOT_FOUND, HttpStatus.BAD_REQUEST, "At least one role must be assigned");
	    }

	    if (userVo.getEMail() == null || userVo.getEMail().isEmpty()) {
	        throw new BloodBankBusinessException(ErrorConstants.INVALID_DATA,HttpStatus.BAD_REQUEST,"Email is required");
	    }
	    
	    RoleVo role1 = userVo.getRoles().iterator().next();
	    if (role1.getRole() == null) {
	        throw new BloodBankBusinessException(
	            ErrorConstants.ROLE_NOT_FOUND,
	            HttpStatus.BAD_REQUEST,
	            "Role cannot be null"
	        );
	    }
	    
	  Set<Role> roles =  userVo.getRoles().stream().map(r -> {
			if(r.getRole() == null) {
				throw new BloodBankBusinessException(ErrorConstants.ROLE_NOT_FOUND ,HttpStatus.BAD_REQUEST,
						ErrorConstants.INVALID_DATA);          
			}
			  Role existingRole = roleRepositary.findByRole(r.getRole().name());
		        if (existingRole != null) {
		            return existingRole;
		        }
		        Role role = Role.builder()
		                .role(r.getRole().name())
		                .description(r.getDescription())
		                .build();

		        return roleRepositary.save(role);
		    }).collect(Collectors.toSet());
				

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
		
		final Integer userId = user.getUserId();
		final String email = user.getEMail();
		
        // 2. If the user has DONOR role, notify donor-service asynchronously
		user.getRoles().stream()
	    .filter(r -> r.getRole().equalsIgnoreCase("DONOR")) // find DONOR role
	    .findFirst()
	    .ifPresent(donorRole -> {
	        userNotificationService.notifyDonorServiceAsync(
	        		userId,
	        		email,
	                donorRole   // passing the Role object
	        );
	    });
		
		UserDto userDto = uModelMapper.map(user, UserDto.class);
		userDto.setStatus(CommonConstants.SUCESS);
		userDto.setMessage(CommonConstants.USER_CREATED_SUCESSFULLY);
		return userDto;
	}
	@Override
	public JWTResponse  login(loginUservo loginUservo) {
		Users user = userRepositary.findByUsername(loginUservo.getUsername());
		if(user == null) {
			throw new BloodBankBusinessException(null);
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
	    SecurityContextHolder.getContext().setAuthentication(authentication);
          String jwt = null;
		  if(authentication.isAuthenticated())
              jwt =   jwtServcie.generateToken(user);
		  
           return  JWTResponse.builder()
                                  .accesToken(jwt)
                                  .token(token.getToken())
                                  .build();
	}

	@Override
	@Transactional
	public UserDto getUsersById(Integer userId) {
		// TODO Auto-generated method stub
//		CommonUtils.verifyUserId(String.valueOf(userId));
		log.debug("user id verified {}:" + userId);
	  Users user = userRepositary.findByUserIdAndIsActiveAndIsPhoneNumberVerified(userId , true , true)  
			  .orElseThrow(() ->  new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,
						ErrorConstants.INVALID_DATA));
	  log.debug("retriveing user from db {} : " + user.getUserId());
	  UserDto uDto = new UserDto();

	  uDto = UserDto.builder()
			  .fullname(user.getFullName().getFirstName() + " " + user.getFullName().getSecondName()+ " " + user.getFullName().getLastName())
			  .username(user.getUsername())
			  .phoneNumber(user.getPhoneNumber())
			  .isPhoneNumberVerified(user.getIsPhoneNumberVerified())
			  .gender(user.getGender())
			  .eMail(user.getEMail())
			  .addressType(user.getAddressType())
			  .dateOfBirth(user.getDateOfBirth())
			  .updatedAt(user.getUpdatedAt())
			  .bio(user.getBio())
			  .wantToDonate(user.getWantToDonate())
			  .build();
		return uDto;
	}

	@Override
	@Transactional
	public UserDto updateUsers(Integer userId, UpdateRequestVO updateRequestVO) {
		// TODO Auto-generated method stub
//		CommonUtils.verifyUserId(String.valueOf(userId));  
		  Users user = userRepositary.findByUserIdAndIsActiveAndIsPhoneNumberVerified(userId , true , true)  
				  .orElseThrow(() ->  new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,
							ErrorConstants.INVALID_DATA));
		  
		  Set<Role> roles =  Optional.ofNullable(updateRequestVO.getRoles()).orElse(Collections.emptySet())
				.stream().map(r -> {
				if(r.getRole() == null) {
					throw new BloodBankBusinessException(ErrorConstants.ROLE_NOT_FOUND ,HttpStatus.BAD_REQUEST,
							ErrorConstants.INVALID_DATA);          
				}
					 Role  role = Role.builder()
							.role(r.getRole().name())
							.description(r.getDescription())
							.build(); 
					roleRepositary.save(role);
					return role;
			}).collect(Collectors.toSet());
		  
		  Optional.ofNullable(updateRequestVO.getFullname()).ifPresent(user::setFullName);
		  Optional.ofNullable(updateRequestVO.getGender()).map(Enum::toString).ifPresent(user::setGender);
		  Optional.ofNullable(updateRequestVO.getEMail()).ifPresent(user::setEMail);
		  Optional.ofNullable(updateRequestVO.getAddressType()).map(Enum::toString).ifPresent(user::setAddressType);
		  Optional.ofNullable(updateRequestVO.getAddress()).ifPresent(user::setAddress);
		  Optional.ofNullable(updateRequestVO.getDateOfBirth()).ifPresent(user::setDateOfBirth);
		  Optional.ofNullable(updateRequestVO.getWantToDonate()).ifPresent(user::setWantToDonate);
		
		  List<String> existingRoleList = user.getRoles().stream().map(role -> role.getRole().toUpperCase()).collect(Collectors.toList());
			 boolean exists =  roles.stream().map(r -> r.getRole().toUpperCase()).anyMatch(rolename -> existingRoleList.stream().anyMatch(existing -> existing.equalsIgnoreCase(rolename)));
		  if (!roles.isEmpty() && !exists) {
			    user.getRoles().addAll(roles);
			}
		  user.setUpdatedAt(LocalDateTime.now()); 
	      user = userRepositary.save(user);
	      return mapperHelper.userToDto(user);
	}

	@Override
//	@CacheEvict(value = "users", key = "#userId")
	public String deleteUser(Integer userId) {
		// TODO Auto-generated method stub
//		CommonUtils.verifyUserId(String.valueOf(userId));
	  Users user = 	userRepositary.findByUserIdAndIsActiveAndIsPhoneNumberVerified(userId ,true,true)
		.orElseThrow(() ->  new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,
				ErrorConstants.INVALID_DATA));
	  
	    if(Boolean.TRUE.equals(user.getIsActive()) && Boolean.TRUE.equals(user.getIsPhoneNumberVerified())) {
	    user.setIsActive(false);
		user.setActiveStatus("PENDING_APPROVAL");
		user.setIsPhoneNumberVerified(false);
		userRepositary.save(user);
	    }
	    else {
	    	throw new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,
					ErrorConstants.INVALID_DATA);
	    }
		log.info("deleted user .." + userId);
	
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
	    RefreshToken refreshToken = refreshTokenRepositary.findByToken(request.getRefreshToken())
	            .orElseThrow(() -> new BloodBankBusinessException(null));

	    if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
	    	refreshToken = createOrUpdateRefreshToken(refreshToken.getUser());
	    }

	    String newAccessToken = jwtServcie.generateToken(refreshToken.getUser());

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
	@Override
	public DonorResponseDto getDonorRole(Integer userId) {
		// TODO Auto-generated method stub
		  Users user = userRepositary.findByUserIdAndIsActiveAndIsPhoneNumberVerified(userId , true , true)  
				  .orElseThrow(() ->  new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,
							ErrorConstants.INVALID_DATA));
		Optional<Role> donorExists = user.getRoles().stream().filter(role -> role.getRole().equalsIgnoreCase(RoleType.DONOR.name())).findFirst();
		if(!donorExists.isPresent()) {
			new BloodBankBusinessException(ErrorConstants.ROLE_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,
					ErrorConstants.INVALID_DATA);
		}
		return DonorResponseDto.builder()
				.userId(user.getUserId())
				.fullname(user.getFullName().toString())
				.username(user.getUsername())
				.phoneNumber(user.getPhoneNumber())
				.gender(user.getGender())
				.eMail(user.getEMail())
				.address(user.getAddress().toString())
				.dateOfBirth(user.getDateOfBirth())
				.bio(user.getBio())
				.wantToDonate(user.getWantToDonate())
				.role(donorExists.get().getRole())
				.build();
	}


}
