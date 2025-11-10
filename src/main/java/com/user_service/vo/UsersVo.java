package com.user_service.vo;

import java.time.LocalDate;
import java.util.Set;

import com.common.enums.AddressType;
import com.common.enums.BloodGroupType;
import com.common.enums.GenderType;
import com.common.enums.LogInType;
import com.common.enums.StatusType;
import com.common.vo.RoleVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.user_service.entities.Address;
import com.user_service.entities.FullName;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsersVo {
	
	private FullName fullname;
	
	@Size(min =2 , max = 15)
	@NotNull
	private String username;
	
	@Size(min =2 , max = 15 , message = "password must be between 2 and 15 characters")
	@NotNull(message = "password cannot be null")
	private String password;
	@Size(min = 2, max = 15, message = "Re-password must be between 2 and 15 characters")
	@NotNull(message = "Re-password cannot be null")
	private String rePassword;
	
	private BloodGroupType bloodGroup;
	
	@NotNull(message = "Phone number cannot be null")
	@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be exactly 10 digits")
	private String phoneNumber;
	
    private Boolean isPhoneNumberVerified;
    @NotNull
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    
	@Email(message = "Invalid email format")
	@NotNull
	private String eMail;
	
	@Column(nullable = false)
	@NotNull(message = "Address Type cant be null")
    @Enumerated(EnumType.STRING)
	private AddressType addressType;
	
    @Embedded
	private Address address ;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	private Boolean isActive;
	
	private Long loginCount;
	
	private String resetToken;
	
	private String bio;
	
	private StatusType activeStatus;
	@Enumerated
	private LogInType logInProvider;
	
	private Boolean wantToDonate; 
	
//	private List<UserHistoryVo> userHistoryvo;
//	
	private Set<RoleVo> roles;

}
