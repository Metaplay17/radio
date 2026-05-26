package org.example.security;

import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.example.entities.User;
import org.example.exceptions.InvalidJwtException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;

@Service
public class JwtService {

    private long expirationSeconds;
    private String secretKey;
    SecretKey key;

    public JwtService(Environment environment) {
        expirationSeconds = Long.parseLong(environment.getProperty("jwt.expiration-seconds", "86400"));
        secretKey = environment.getProperty("jwt.secret-key");
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }
    
    public String generateToken(User user) {
        return Jwts.builder()
            .claim("username", user.getUsername())
            .issuer("This Application")
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusSeconds(expirationSeconds)))
            .signWith(key, Jwts.SIG.HS256)
            .compact();
    }

    public String parseUsernameFromToken(String token) {
        try {
            return String.valueOf(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().get("username"));
        } catch (MalformedJwtException | SecurityException | ExpiredJwtException ex) {
            throw new InvalidJwtException("Токен не валиден");
        }
    }
}
