package com.user_service.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class FullName {
	
	private String firstName;
	private String secondName;
	private String lastName;

	@Override
	public String toString() {
		return firstName + " , "  + secondName +" ," +  lastName;
	}
}
