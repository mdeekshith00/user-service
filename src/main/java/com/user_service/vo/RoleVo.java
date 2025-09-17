package com.user_service.vo;

import com.common.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleVo {
	
     @Enumerated
     @NotNull(message = " role cannot be empty:")
	private RoleType role;
	
	private String description;
	
}
