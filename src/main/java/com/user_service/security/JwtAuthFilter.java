package com.user_service.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.common.security.JWTService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("🛬 [USER-SERVICE] Incoming request: " + request.getRequestURI());

        String authHeader = request.getHeader("Authorization");
        System.out.println("🔍 [USER-SERVICE] Authorization Header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ [USER-SERVICE] No or bad token. Passing request along unauthenticated.");
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        System.out.println("🧪 [USER-SERVICE] Extracted token: " + token);

        boolean valid = jwtService.validateToken(token);
        System.out.println("🧪 [USER-SERVICE] jwtService.validateToken() = " + valid);

        if (!valid) {
            System.out.println("❌ [USER-SERVICE] Token invalid/expired. Not authenticating user.");
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtService.extractAllClaims(token);
        String username = claims.getSubject();
        Object rolesObj = claims.get("roles");

        System.out.println("👤 [USER-SERVICE] User from token: " + username);
        System.out.println("👑 [USER-SERVICE] Roles from token: " + rolesObj);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if (rolesObj instanceof List<?> list) {
            for (Object r : list) {
                if (r != null) {
                    authorities.add(new SimpleGrantedAuthority(r.toString()));
                }
            }
        } else if (rolesObj instanceof String str) {
            for (String r : str.split(",")) {
                if (!r.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority(r.trim()));
                }
            }
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(authToken);

        System.out.println("✅ [USER-SERVICE] Authentication set in SecurityContext");

        filterChain.doFilter(request, response);
    }
}
