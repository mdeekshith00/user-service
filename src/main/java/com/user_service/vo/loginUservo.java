package com.user_service.vo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class loginUservo {
	@NotNull(message = "username cannot be null:")
    private String username;
	@NotNull(message = "password cant be null:")
	private String password;
}
