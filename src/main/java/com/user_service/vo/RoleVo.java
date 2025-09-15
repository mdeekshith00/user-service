package com.user_service.vo;

import com.common.enums.RoleType;

import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleVo {
	
     @Enumerated
     @NotNull(message = " role cannot be empty:")
	private RoleType role;
	
	private String description;
	
}
