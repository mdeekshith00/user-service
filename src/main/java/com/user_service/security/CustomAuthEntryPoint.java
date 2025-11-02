//package com.user_service.security;
//
//import org.springframework.security.web.AuthenticationEntryPoint;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@Component
//public class CustomAuthEntryPoint implements AuthenticationEntryPoint {
//
//
//	@Override
//	public void commence(HttpServletRequest request, HttpServletResponse response,
//			org.springframework.security.core.AuthenticationException authException)
//			throws java.io.IOException, ServletException {
//		// TODO Auto-generated method stub
//		 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//	        response.setContentType("application/json");
//	        response.getWriter().write("{\"error\": \"Unauthorized: Invalid or missing token\"}");
//
//		
//	}
//
//}
