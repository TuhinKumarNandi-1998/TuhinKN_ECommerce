package com.tuhinK.eCommerce.auth.jwt;

import com.tuhinK.eCommerce.auth.user.EComUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtUtility {

    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;

    @Value("${auth.token.expirationInMills}")
    private int jwtExpirationTime;

    public String generateTokenForUser(Authentication authentication) {
        // 1. Get the authenticated user's details
        EComUserDetails userPrinciple = (EComUserDetails) authentication.getPrincipal();

        // 2. Extract their roles into a simple List of Strings (e.g., ["ROLE_USER", "ROLE_ADMIN"])
        List<String> roles = userPrinciple.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(userPrinciple.getUsername())
                .claim("id", userPrinciple.getId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration((new Date(new Date().getTime() + jwtExpirationTime)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(authToken);

            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException
                | IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        }
    }

    public String getUsernameFromJwtToken(String authToken) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(authToken)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts user details directly from the JWT claims without querying the
     * database.
     * The JWT already contains the user's id, username (subject), and roles,
     * so we can reconstruct the EComUserDetails object from these claims.
     */
    @SuppressWarnings("unchecked")
    public EComUserDetails extractUserDetailsFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        Long id = claims.get("id", Long.class);
        List<String> roles = claims.get("roles", List.class);

        List<GrantedAuthority> grantedAuthorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new EComUserDetails()
                .setId(id)
                .setEmail(username)
                .setPassword(null)
                .setAuthorities(grantedAuthorities);
    }
}
