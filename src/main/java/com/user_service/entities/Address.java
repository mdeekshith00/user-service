package com.user_service.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Address {
	
	 private String line1;     // Flat, street, etc.
	 private String city;
     private String state;
	 private String country;
	 private String pincode;
	 
	  @Override
	    public String toString() {
	        return line1 + ", " + city + ", " + state + ", " + country + " - " + pincode;
	    }

}
