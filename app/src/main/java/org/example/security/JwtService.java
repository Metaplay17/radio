package org.example.security;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.example.entities.User;
import org.example.exceptions.InvalidJwtException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;

@Service
public class JwtService {

    private long expirationSeconds;
    private String secretKey;
    SecretKey key;

    public JwtService(Environment environment) {
        expirationSeconds = Long.parseLong(environment.getProperty("jwt.expiration-seconds", "86400"));
        secretKey = environment.getProperty("jwt.secret-key");
        key = new SecretKeySpec(Base64.getDecoder().decode(secretKey), Jwts.SIG.HS256.toString());
    }
    
    public String generateToken(User user) {
        return Jwts.builder()
            .claim("userId", user.getId())
            .issuer("This Application")
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusSeconds(expirationSeconds)))
            .signWith(key)
            .compact();
    }

    public Long parseUserIdFromToken(String token) {
        try {
            return Long.valueOf(((Claims)Jwts.parser().decryptWith(key).build().parse(token).getPayload()).getSubject());
        } catch (MalformedJwtException | ClassCastException | SecurityException | ExpiredJwtException | IllegalArgumentException ex) {
            throw new InvalidJwtException("Токен не валиден");
        }
    }
}
