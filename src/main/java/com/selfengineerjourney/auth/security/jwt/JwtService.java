package com.selfengineerjourney.auth.security.jwt;

import com.selfengineerjourney.auth.dto.JwtResponse;
import com.selfengineerjourney.auth.entity.User;
import com.selfengineerjourney.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final UserRepository userRepository;

    // Expiration access token = 15 minutes
    private final long accessTokenExpiration = 15 * 60 * 1000;

    @Value("${app.security.access-token.secret-key}")
    private String secretKey;

    public JwtResponse generateJwtToken(User user) {
        return new JwtResponse(generateAccessToken(user));
    }

    public String generateAccessToken(User userDetails) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("typ", "Bearer");
        return generateAccessToken(userDetails, claims);
    }

    public String generateAccessToken(User userDetails, Map<String, Object> extraClaims) {
        return buildToken(extraClaims, userDetails,  accessTokenExpiration);
    }

    public String buildToken(Map<String, Object> extraClaims, User userDetails, long expiration) {
        return Jwts.builder()
                .subject(Long.toString(userDetails.getId()))
                .claims(extraClaims)
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSecretKey())
                .compact();
    }

    public String extractSubject(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        Long userId = Long.parseLong(extractSubject(token));
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getUsername().equals(userDetails.getUsername()) && !isExpired(token);
    }

    public Boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }
}
