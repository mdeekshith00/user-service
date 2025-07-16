package com.user_service.annatation;

import java.util.Set;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class BloodGroupValidator implements ConstraintValidator<ValidBloodGroup, String>{
	
	  private static final Set<String> VALID_GROUPS = Set.of(
	            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
	    );

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		// TODO Auto-generated method stub
        return value != null && VALID_GROUPS.contains(value.toUpperCase());
	}

}
