package com.user_service.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.common.security.JWTService;

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
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ SKIP JWT CHECK FOR PUBLIC ENDPOINTS
        if (
            path.equals("/user/sign-up") ||
            path.equals("/user/sign-in") ||
            path.equals("/user/refresh-token")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        // 🔐 Normal JWT logic below
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        // validate token, set authentication, etc...

        filterChain.doFilter(request, response);
    }

}
