package com.user_service.vo;

import java.io.Serializable;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class JWTRoleDetails  implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String username;
	String role;
	String phoneNumber;
	String userId;
	

}
