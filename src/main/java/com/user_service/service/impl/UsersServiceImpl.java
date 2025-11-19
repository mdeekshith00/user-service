package com.user_service.service.impl;


import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.springframework.transaction.annotation.Transactional;

import com.common.constants.CommonConstants;
import com.common.constants.ErrorConstants;
import com.common.dto.DonorResponseDto;
import com.common.enums.RoleType;
import com.common.enums.StatusType;
import com.common.exception.BloodBankBusinessException;
import com.common.security.JWTService;
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
import com.user_service.util.HelperMethods;
import com.user_service.util.KafkaHelpers;
import com.user_service.vo.UpdateRequestVO;
import com.user_service.vo.UsersVo;
import com.user_service.vo.loginUservo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService , RefreshTokenService {
	
	private final UserRepositary userRepositary;
	private final RoleRepositary roleRepositary;
	private final JWTService jwtServcie;
	private final ModelMapper uModelMapper;
	private final AuthenticationManager authManager;
	private final RefreshTokenrepositary refreshTokenRepositary;
	private final MapperHelper mapperHelper;
	private final KafkaHelpers kafkaHelper;
	private final HelperMethods helperMethod;
	
	private  BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
	
	
	@Override
	@Transactional 
	public UserDto register(UsersVo userVo) {
		// TODO Auto-generated method stub
	   Optional<Users> existingUser = userRepositary.findByPhoneNumber(userVo.getPhoneNumber());
	   if(existingUser.isPresent()) {
			throw new BloodBankBusinessException(ErrorConstants.USER_DETAILS_ALREADY_EXISTS ,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);          
	   }
	   log.info("creating new user");
	   
	   if (userVo.getUsername() == null || userVo.getUsername().isEmpty()) {
	        throw new BloodBankBusinessException(ErrorConstants.USERNAME_SHOULD_REQUIRED, HttpStatus.BAD_REQUEST, ErrorConstants.INVALID_DATA);
	    }
	    if (userVo.getPassword() == null || userVo.getPassword().isEmpty()) {
	        throw new BloodBankBusinessException(ErrorConstants.PASSWORD_SHOILD_REQUIRED, HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);
	    }
	    if (userVo.getRePassword() == null || userVo.getRePassword().isEmpty()) {
	        throw new BloodBankBusinessException(ErrorConstants.PASSWORD_SHOILD_REQUIRED, HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);
	    }
	    if (userVo.getEMail() == null || userVo.getEMail().isEmpty()) {
	        throw new BloodBankBusinessException(ErrorConstants.EMAIL_SHOULD_REQUIRED,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);
	    }
	    if(!userVo.getPassword().contentEquals(userVo.getRePassword())) {
	        throw new BloodBankBusinessException(ErrorConstants.INVALID_DATA,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);

	    }
	    
	  Set<Role> roles =  userVo.getRoles().stream().map(r -> {
			if(r.getRole() == null) {
				throw new BloodBankBusinessException(ErrorConstants.ROLE_NOT_FOUND ,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);          
			}
		        Role role = Role.builder()
		                .role(r.getRole().name())
		                .description(r.getDescription())
		                .build();

		        return roleRepositary.save(role);
		    }).collect(Collectors.toSet());
				

		 Users user = Users.builder()
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
		log.info("user sucessfully registered in Db {}" , user.getUserId());
		
		final Integer userId = user.getUserId();
		final String email = userVo.getEMail().toString();
		
	    // Notify downstream services based on roles
	    user.getRoles().forEach(role -> {
	        String roleName = role.getRole();
	        try {
	            switch (roleName.toUpperCase()) {
	                case "DONOR":
	                    log.info("Notifying donor-service for userId {}", userId);
	                    // REST call (immediate)
	                    helperMethod.notifyDonorService(userId, email, userVo);
	                    // Kafka fallback/event
//	                    kafkaHelper.publishUserEvent(userId, email, "DONOR_CREATED");
	                    break;

	                case "HOSPITAL_ADMIN":
	                    log.info("Notifying hospital-service for hospital admin userId {}", userId);
	                    helperMethod.notifyHospitalServiceForAdmin(userId, email, userVo);
//	                    kafkaHelper.publishUserEvent(userId, email, "HOSPITAL_ADMIN_CREATED");
	                    break;

	                case "HOSPITAL_STAFF":
	                    log.info("Notifying hospital-service for staff userId {}", userId);
	                    helperMethod.notifyHospitalServiceForStaff(userId, email, userVo);
//	                    kafkaHelper.publishUserEvent(userId, email, "HOSPITAL_STAFF_CREATED");
	                    break;

	                case "CAMP_COORDINATOR":
	                    log.info("Notifying camp-service for coordinator userId {}", userId);
	                    helperMethod.notifyCampServiceCoordinator(userId, email, userVo);
//	                    kafkaHelper.publishUserEvent(userId, email, "CAMP_COORDINATOR_CREATED");
	                    break;

	                case "VOLUNTEER":
	                    log.info("Notifying camp-service for volunteer userId {}", userId);
	                    helperMethod.notifyCampServiceVolunteer(userId, email, userVo);
//	                    kafkaHelper.publishUserEvent(userId, email, "VOLUNTEER_CREATED");
	                    break;

	                case "AUDITOR":
	                    // Auditors may not need a domain entity, just an event for auditing systems
	                    log.info("Publishing AUDITOR_CREATED event for userId {}", userId);
//	                    kafkaHelper.publishUserEvent(userId, email, "AUDITOR_CREATED");
	                    break;

	                case "ADMIN":
	                case "SYSTEM":
	                    // No domain entity creation required - just produce event for visibility
	                    log.info("Publishing {} event for userId {}", roleName, userId);
//	                    kafkaHelper.publishUserEvent(userId, email, roleName + "_GRANTED");
	                    break;

	                default:
	                    log.warn("Role {} not mapped to downstream action. Skipping.", roleName);
	            }
	        } catch (Exception ex) {
	            // Don't rollback user creation for downstream failures.
	            log.error("Error while notifying for role {} for userId {} : {}", roleName, userId, ex.getMessage(), ex);
	            // Publish a failure event to kafka for later reconciliation
//	            kafkaProducer(userId, email, roleName + "_CREATE_FAILED", ex.getMessage());
	        }
	    });

		log.info("Sending event to Notification-service using kafka producer...");
		
		UserDto userDto = uModelMapper.map(user, UserDto.class);
		userDto.setStatus(CommonConstants.SUCESS);
		userDto.setMessage(CommonConstants.USER_CREATED_SUCESSFULLY);
		return userDto;
	}
	
	@Override
	public JWTResponse  login(loginUservo loginUservo) {
		Users user = userRepositary.findByUsername(loginUservo.getUsername());
		if(user == null) {
			throw new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,
					ErrorConstants.INVALID_DATA);
		}
		user.setIsActive(Boolean.TRUE);
		user.setLastLogin(Timestamp.from(Instant.now()));
		user.setIsPhoneNumberVerified(Boolean.TRUE);
        user.setLoginCount(Optional.ofNullable(user.getLoginCount()).map(count -> count+1).orElse((long) 1) );
        user.setActiveStatus(StatusType.ACTIVE.name());
        
		userRepositary.save(user);
		log.info("User in DB: " + user.getUsername());
		
		log.info("Password in DB: " + user.getPassword());
		log.info("Input password: " + loginUservo.getPassword());
		log.info("Matches? " + encoder.matches(loginUservo.getPassword(), user.getPassword()));
		
		Authentication authentication  = 
				authManager.authenticate(new UsernamePasswordAuthenticationToken(loginUservo.getUsername(), loginUservo.getPassword()));
		RefreshToken token =  createrefreshToken(user);
	    SecurityContextHolder.getContext().setAuthentication(authentication);
          String jwt = null;
          
          Map<String, Object> claims = new HashMap<>();
          claims.put("userId", user.getUserId());
          claims.put("roles", user.getRoles().stream().map(Role::getRole).toList());
          claims.put("phone", user.getPhoneNumber());

		  if(authentication.isAuthenticated())
              jwt =   jwtServcie.generateTokenFromClaims(claims);
		  
           return  JWTResponse.builder()
                                  .accesToken(jwt)
                                  .token(token.getToken())
                                  .build();
	}

	@Override
	@Transactional(readOnly = true)
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
		log.info("Fetching user details to upadte {}" , userId);
		 Users user = userRepositary.findByUserIdAndIsActiveAndIsPhoneNumberVerified(userId , true , true)  
				  .orElseThrow(() ->  new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,
							ErrorConstants.INVALID_DATA));
		  
		  Set<RoleVo> roleVo =  Optional.ofNullable(updateRequestVO.getRoles()).orElse(Collections.emptySet());
		  
           for(RoleVo vo : roleVo) {
             if (vo == null || vo.getRole() == null) {
                log.debug("Skipping null role entry.");
                continue;
            }
           boolean existingRole = user.getRoles().stream()
        		   .anyMatch(r->r.getRole().equalsIgnoreCase(vo.getRole().name()));
           
           if (existingRole) {
               throw new BloodBankBusinessException(ErrorConstants.ROLE_ALREADY_EXISTS,HttpStatus.BAD_GATEWAY,ErrorConstants.INVALID_DATA);
           }
           Role newRole = Role.builder()
                   .role(vo.getRole().toString())
                   .description(vo.getDescription())
                   .build();
           
           log.info("Saving new role  {}" , newRole.getRole());
           roleRepositary.save(newRole); 
           log.info("newRole Adding to User {}" , user.getUserId());
           user.getRoles().add(newRole);
        } 
		  Optional.ofNullable(updateRequestVO.getFullname()).ifPresent(user::setFullName);
		  Optional.ofNullable(updateRequestVO.getGender()).map(Enum::toString).ifPresent(user::setGender);
		  Optional.ofNullable(updateRequestVO.getEMail()).ifPresent(user::setEMail);
		  Optional.ofNullable(updateRequestVO.getAddressType()).map(Enum::toString).ifPresent(user::setAddressType);
		  Optional.ofNullable(updateRequestVO.getAddress()).ifPresent(user::setAddress);
		  Optional.ofNullable(updateRequestVO.getDateOfBirth()).ifPresent(user::setDateOfBirth);
		  Optional.ofNullable(updateRequestVO.getWantToDonate()).ifPresent(user::setWantToDonate);
		  user.setUpdatedAt(LocalDateTime.now()); 
		  log.info("Sucessfully user updated..{}" , user.getUserId());
	      user = userRepositary.save(user);
	      return mapperHelper.userToDto(user);
	}

	@Override
	public String deleteUser(Integer userId) {
		// TODO Auto-generated method stub
//		CommonUtils.verifyUserId(String.valueOf(userId));
	  Users user = 	userRepositary.findByUserIdAndIsActiveAndIsPhoneNumberVerified(userId ,true,true)
		.orElseThrow(() ->  new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA));
      log.debug("fetching user details to deleted .{}",  userId);
	    user.setIsActive(false);
		user.setActiveStatus("PENDING_APPROVAL");
		user.setIsPhoneNumberVerified(false);

		userRepositary.save(user);
		log.info("Sucessfully user deleted from Db :{} : {}", user.getUserId() , user.getUsername());

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
	          .filter(user -> user.getWantToDonate());
	   List<UserDto> dto =    users.stream().map(user -> uModelMapper.map(user, UserDto.class)).collect(Collectors.toList());
		return dto;
	}
	
	@Override
	public String forgotPassword(String username) {
		log.debug("Fetching user for forgot-password.{}" , username);
	Users user = 	userRepositary.findByUsername(username);
	if(user == null) {
		throw new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);
	}
	String resetPassword = null ;
	if(user.getIsActive().equals(Boolean.TRUE) && user.getIsPhoneNumberVerified().equals(Boolean.TRUE)) {
		 resetPassword = user.getPhoneNumber() + UUID.randomUUID()+Instant.now().toString();
		user.setResetToken(resetPassword);
		userRepositary.save(user);
		log.info("Sucessfully reset-password token generated for user {}",user.getUserId());
	} else {
		throw new BloodBankBusinessException(ErrorConstants.USER_NOT_EXISTS ,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);
	}
		return "reset your password with : " + resetPassword;
	}
	
	@Override
	public String resetPassword(String username , String resetPassword , String password) {
		log.debug("Fetching user for forgot-password.{}" , username);
		Users user = 	userRepositary.findByUsername(username);
		if(user == null) {
			throw new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);
		}
		if(user.getIsActive().equals(Boolean.TRUE) && user.getIsPhoneNumberVerified().equals(Boolean.TRUE)) {
			if(user.getResetToken().equalsIgnoreCase(resetPassword)) 
		                     	user.setResetToken(null);
			 else 
				throw new BloodBankBusinessException(ErrorConstants.USER_DETAILS_EXISTS ,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);
			user.setPassword(encoder.encode(password));
			userRepositary.save(user);
			log.info("Sucessfully new  Password updated to user .{}" , user.getUserId());
		} else {
			throw new BloodBankBusinessException(ErrorConstants.USER_DETAILS_NOT_FOUND ,HttpStatus.BAD_REQUEST,ErrorConstants.INVALID_DATA);
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

	    Users user = refreshToken.getUser();

	    Map<String, Object> claims = new HashMap<>();
	    claims.put("userId", user.getUserId());
	    claims.put("roles", user.getRoles().stream().map(Role::getRole).toList());
	    claims.put("phone", user.getPhoneNumber());
 
	    String newAccessToken = jwtServcie.generateTokenFromClaims(claims);

	    return JWTResponse.builder()
	            .accesToken(newAccessToken)
	            .token(refreshToken.getToken()) // keep same refresh token if still valid
	            .build();
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

	@Override
	public RefreshToken createrefreshToken(Users user) {
		// TODO Auto-generated method stub
	    RefreshToken existingToken = refreshTokenRepositary.findByUser(user).get();

	    if (existingToken != null) {
	        existingToken.setToken(UUID.randomUUID().toString());
	        existingToken.setExpiryDate(Instant.now().plusSeconds(3600));
	        return refreshTokenRepositary.save(existingToken);
	    }

	    RefreshToken refreshToken = RefreshToken.builder()
	            .user(user)
	            .token(UUID.randomUUID().toString())
	            .expiryDate(Instant.now().plusSeconds(3600))
	            .build();

	    return refreshTokenRepositary.save(refreshToken);
	}
}
