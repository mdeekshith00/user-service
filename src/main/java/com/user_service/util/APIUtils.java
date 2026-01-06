package com.user_service.util;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Service
public class APIUtils {
	
	private static ObjectMapper mapper;
	
	public static ObjectMapper getMapper() {
		if(mapper == null) {
			mapper = new ObjectMapper();
			mapper.registerModule(new JavaTimeModule());
		}
		return mapper;
	}

}
