package com.user_service.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.user_service.service.impl.UserPrinicipalServiceImpl;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JWTFilter sJWTFilter;
	private final UserPrinicipalServiceImpl userPrinicipalServiceImpl ;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				 .csrf(csrf -> csrf.disable())
				 .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//				 .cors(custom -> {}) // ensure CORS is configured if required
		            .authorizeHttpRequests(auth -> auth
		                    // public endpoints - ensure these match JWTFilter.shouldNotFilter
		                    .requestMatchers("/auth/**" ,"/user/sign-up", "/user/sign-in", "/user/refresh-token").permitAll()
		                    .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
		                    .anyRequest().authenticated()
		                )
		            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					   .httpBasic(basic -> basic.disable())
			            .formLogin(fl -> fl.disable())
//		            .exceptionHandling(ex -> ex
//		            	    .authenticationEntryPoint(new CustomAuthEntryPoint())
//		            	    .accessDeniedHandler(new CustomAccessDeniedHandler())
//		            	)
		            .authenticationProvider(authenticationProvider())
				.addFilterBefore(sJWTFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

    @Bean
    AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userPrinicipalServiceImpl);
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		return provider;
		
	}

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    	return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:8000")); // Gateway
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return (CorsConfigurationSource) source;
    }

}
