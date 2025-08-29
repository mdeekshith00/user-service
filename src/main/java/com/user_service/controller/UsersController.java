package com.user_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.user_service.dto.BaseDto;
import com.user_service.dto.JWTResponse;
import com.user_service.dto.MinUserDto;
import com.user_service.dto.RefreshTokenRequest;
import com.user_service.dto.UserDto;
import com.user_service.service.UsersService;
import com.user_service.vo.UsersVo;
import com.user_service.vo.loginUservo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User APIs", description = "Operations related to Users")
public class UsersController {
	
	private final UsersService userService;
//	private final Executor virtualThreadExecutor;
	
	@PostMapping("/sign-up")
	public ResponseEntity<BaseDto> register(@RequestBody UsersVo userVo) {
		UserDto user = 	userService.register(userVo);
		return  ResponseEntity.status(HttpStatus.CREATED).body(user);
	}
	
	@PostMapping("/sign-in")
	 public ResponseEntity<JWTResponse>  login(@RequestBody loginUservo loginUservo) {
		return ResponseEntity.ok().body(userService.login(loginUservo));
				
	}
	@PostMapping("/refresh-token")
	 public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) { 
			return ResponseEntity.ok().body(userService.refreshToken(request));
	  }
	
	@PostMapping("/forgot-password/{username}")
	public ResponseEntity<String> forgotPassword(@PathVariable String username) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.forgotPassword(username));
	}
	
	@PostMapping("/reset-password/{username}")
	public ResponseEntity<String>  resetPassword(@PathVariable String username  ,@RequestParam String resetPassword ,@RequestParam String password) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.resetPassword(username, resetPassword, password));
	}
	
	@Operation(summary = "Get user by ID", description = "Returns a single user by their ID")
	 @GetMapping("/{userId}")
	public ResponseEntity<BaseDto> getUsersById(@PathVariable Integer userId) {
 
//		return CompletableFuture.supplyAsync(()-> {
//			UserDto user = userService.getUsersById(userId);
//			return ResponseEntity.status(HttpStatus.OK).body(user);
//		} , virtualThreadExecutor );
		return  ResponseEntity.status(HttpStatus.OK).body(userService.getUsersById(userId));
	}
	 
	@PutMapping("/update/{userId}")
	public ResponseEntity<BaseDto> updateUsers(@PathVariable Integer userId,@RequestBody UsersVo userVo) {
		return  ResponseEntity.status(HttpStatus.OK).body(userService.updateUsers(userId, userVo));
	}
	
	@DeleteMapping(path = "/delete/{userId}")
	public ResponseEntity<?> deleteUser(@PathVariable Integer userId) {
		return  ResponseEntity.status(HttpStatus.OK).body(userService.deleteUser(userId));
	}
//	@PreAuthorize("ADMIN")
	@GetMapping(path = "/get-all")
	public  ResponseEntity<List<?>> getAllUsers() {
	   return  ResponseEntity.status(HttpStatus.OK).body(userService.getAllUsers());
	}
}
