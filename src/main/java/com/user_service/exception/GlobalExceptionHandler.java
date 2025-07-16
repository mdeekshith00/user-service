package com.user_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UserDetailsNotFoundException.class)
	public ResponseEntity<?> handleUserDetailsNotFoundException(UserDetailsNotFoundException e) {
		ErrorResponse response = new ErrorResponse("Users" , "uId" , e.getMessage());
		return ResponseEntity.ok(response);
		}
	
	@ExceptionHandler(DetailsNotFoundException.class)
	public ResponseEntity<?> handleDetailsNotFoundException(DetailsNotFoundException e) {
		ErrorResponse response = new ErrorResponse("User-service" , "null" , e.getMessage());
		return ResponseEntity.ok(response);
	}
	@ExceptionHandler() 
	public ResponseEntity<?> handleException(Exception ex) {
		ErrorResponse response = new ErrorResponse("User-service" , "null" , ex.getMessage());
		return ResponseEntity.ok(response);

	}
	

}
