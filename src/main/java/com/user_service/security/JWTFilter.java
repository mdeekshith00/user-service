package com.user_service.security;

import java.io.IOException;
import java.util.List;

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
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
@RequiredArgsConstructor
public class JWTFilter  extends OncePerRequestFilter {
	
//	 log.info("JWTFilter bean created!");
	 
	
	private final UserPrinicipalServiceImpl JuserServiceImpl;
	private final JWTService jJwtService;
	
    private static final List<String> PUBLIC_PATHS = List.of(
            "/user/sign-up",
            "/user/sign-in",
            "/user/refresh-token",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
        );
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // quick startsWith checks for swagger and public endpoints
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		   try {
	            String authHeader = request.getHeader("Authorization");
	            log.info("Authorization Header: {}", authHeader);

	            if (authHeader != null && authHeader.startsWith("Bearer ")) {
	                String token = authHeader.substring(7);
	                String username = jJwtService.extractUserName(token);
	                log.debug("Token username: {}", username);

	                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
	                    UserDetails userDetails = JuserServiceImpl.loadUserByUsername(username);
	                    if (jJwtService.validateToken(token, userDetails)) {
	                        UsernamePasswordAuthenticationToken authToken =
	                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
	                        SecurityContextHolder.getContext().setAuthentication(authToken);
	                        log.info("Authentication set in SecurityContext for user: {}", username);
	                    } else {
	                        log.warn("Token validation failed for user: {}", username);
	                    }
	                }
	            } else {
	                log.debug("No Bearer token found in Authorization header");
	            }

	            log.info("Before filterChain.doFilter for URI {}", request.getRequestURI());
	            filterChain.doFilter(request, response);
	            log.info("After filterChain.doFilter for URI {}", request.getRequestURI());
	        } catch (Exception ex) {
	            log.error("Exception in JWTFilter: {}", ex.getMessage(), ex);
	            // pass exception along for controller/advice or let security handle it
	            request.setAttribute("filter.error", ex);
	            filterChain.doFilter(request, response);
	        }
	    }
}
