package com.user_service.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.user_service.service.impl.JWTService;
import com.user_service.service.impl.UserPrinicipalServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JWTFilter  extends OncePerRequestFilter {
	
	private final UserPrinicipalServiceImpl JuserServiceImpl;
	private final JWTService jJwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String authHeader = request.getHeader("Authorization");
		String token = null;
		String username = null;
		
		if(authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
			username = jJwtService.extractUserName(token);
		}
		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails  userDetails = JuserServiceImpl.loadUserByUsername(username);
		     if(jJwtService.validateToken(token , userDetails)) {
		    	 	UsernamePasswordAuthenticationToken authToken = new
		    	 			UsernamePasswordAuthenticationToken(userDetails, null ,  userDetails.getAuthorities());
		    	 	authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		    	 	SecurityContextHolder.getContext().setAuthentication(authToken);
		     }
		}
		doFilter(request, response, filterChain);
	}

}
