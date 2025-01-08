package com.example.outsourcing.domain.common.util;

import com.example.outsourcing.domain.user.entity.User.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final long TOKEN_TIME = 60 * 60 * 1000L;

  @Value("${jwt.secret.key}")
  private String secretKey;

  private Key key;
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  @PostConstruct
  public void init() {
    if (!StringUtils.hasText(secretKey)) {
      log.error("JWT secret key is null or empty");
      throw new IllegalArgumentException("JWT secret key must not be null or empty");
    }
    try {
      byte[] bytes = Base64.getDecoder().decode(secretKey);
      key = Keys.hmacShaKeyFor(bytes);
      log.info("JWT secret key initialized successfully");
    } catch (IllegalArgumentException e) {
      log.error("Failed to decode JWT secret key: {}", e.getMessage());
      throw new IllegalArgumentException("Invalid JWT secret key");
    }
  }

  public String createToken(Long userId, String email, UserRole userRole) {
    Date date = new Date();

    return BEARER_PREFIX
        + Jwts.builder()
            .setSubject(String.valueOf(userId))
            .claim("email", email)
            .claim("userRole", userRole)
            .setExpiration(new Date(date.getTime() + TOKEN_TIME))
            .setIssuedAt(date)
            .signWith(key, signatureAlgorithm)
            .compact();
  }

  public String substringToken(String tokenValue) {
    if (!StringUtils.hasText(tokenValue)) {
      throw new IllegalArgumentException("Token must not be null or empty");
    }
    if (!tokenValue.startsWith(BEARER_PREFIX)) {
      throw new IllegalArgumentException("Token does not start with Bearer");
    }
    if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
      return tokenValue.substring(7);
    }
    throw new IllegalArgumentException("Not Found Token");
  }

  public Claims extractClaims(String token) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    } catch (Exception e) {
      log.error("Failed to parse JWT token: {}", e.getMessage());
      throw new IllegalArgumentException("Invalid or expired JWT token");
    }
  }
}
