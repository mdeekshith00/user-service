package com.user_service.controller;

<<<<<<< HEAD
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UsersController {

=======
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

import com.common.dto.BaseDTO;
import com.common.dto.DonorResponseDto;
import com.user_service.dto.JWTResponse;
import com.user_service.dto.RefreshTokenRequest;
import com.user_service.dto.UserDto;
import com.user_service.service.UsersService;
import com.user_service.vo.UpdateRequestVO;
import com.user_service.vo.UsersVo;
import com.user_service.vo.loginUservo;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UsersController {
	
	private final UsersService userService;
	
	@PostMapping("/sign-up")
	public ResponseEntity<BaseDTO> register(@RequestBody @Valid UsersVo userVo) {
		UserDto user = 	userService.register(userVo);
		return  ResponseEntity.status(HttpStatus.CREATED).body(user);
	}
	
	@PostMapping("/sign-in")
	 public ResponseEntity<JWTResponse>  login(@RequestBody @Valid loginUservo loginUservo) {
		return ResponseEntity.ok().body(userService.login(loginUservo));
				
	}
	@PostMapping("/refresh-token")
	 public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest request) { 
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
	
	 @GetMapping("/{userId}")
	public ResponseEntity<UserDto> getUsersById(@PathVariable Integer userId) {
		return  ResponseEntity.status(HttpStatus.OK).body(userService.getUsersById(userId));
	}
	 
	@PutMapping("/update/{userId}")
	public ResponseEntity<BaseDTO> updateUsers(@PathVariable Integer userId, @Valid @RequestBody UpdateRequestVO updateRequestVO) {
		return  ResponseEntity.status(HttpStatus.OK).body(userService.updateUsers(userId, updateRequestVO));
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
	@GetMapping("/donor-details/{userId}")
	public ResponseEntity<DonorResponseDto> getDonorRole(@PathVariable Integer userId) {
		 return  ResponseEntity.status(HttpStatus.OK).body(userService.getDonorRole(userId));
	}
>>>>>>> 461be25bf30961215b2a0ec748bf111b14d46c50
}
