package com.user_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class ErrorResponse {
	private String resourceName;
	private String fieldName;
	private String message;

}
