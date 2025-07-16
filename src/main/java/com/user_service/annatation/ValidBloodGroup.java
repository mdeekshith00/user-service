package com.user_service.annatation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = BloodGroupValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBloodGroup {
	String message() default  "Invalid Blood Group... Allowed : A+, A-, B+, B-, AB+, AB-, O+, O-";
	   Class<?>[] groups() default {};
	    Class<? extends Payload>[] payload() default {};

}
