package com.user_service.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTService {
	
	@Value("${jwt.secret}")
	private String secertKey;
	
//	@Value("${jwt.expiration}")
//	private Date expirationTime;
	
	public String generateToken(String username) {
		Map<String , Object> claims = new HashMap<>();
//		claims.put(username, username) 
		return Jwts
				.builder()
				.setClaims(claims)
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis() + 36000))
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				.compact();
		
	}

	private Key getSignKey() {
		// TODO Auto-generated method stub
		byte[] keyBytes = Decoders.BASE64.decode(secertKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractUserName(String token) {
		// TODO Auto-generated method stub
		return extractClaim(token, Claims::getSubject);
	}

	private  <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
	       final Claims claims = extractAllClaims(token);
	       return claimResolver.apply(claims);
	   }

	  private Claims extractAllClaims(String token) {
	         return Jwts.parserBuilder()
	             .setSigningKey(getSignKey())
	             .build()
	             .parseClaimsJws(token)
	             .getBody() ;
	     }

	public boolean validateToken(String token, UserDetails extractUserName) {
		// TODO Auto-generated method stub
		final String username = extractUserName(token);
		return (username.equalsIgnoreCase(extractUserName.getUsername()) && !isTokenValid(token));
	}

	private boolean isTokenValid(String token) {
		// TODO Auto-generated method stub
	return extractExpiration(token).before(new Date());

	}

	private Date extractExpiration(String token) {
		// TODO Auto-generated method stub
		return extractClaim(token, Claims::getExpiration);
	}
	

}
