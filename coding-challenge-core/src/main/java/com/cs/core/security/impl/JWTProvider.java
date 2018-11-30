package com.cs.core.security.impl;

import com.cs.domain.auth.PatientCredentials;
import com.cs.domain.auth.Role;
import com.cs.domain.auth.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JWTProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private String expirationTime;


    public String generateToken(User user) {
        var claims = new HashMap<String, Object>();
        claims.put("roles", user.getRoles());
        return doGenerateToken(claims, user.getUsername());
    }

    public String generateToken(PatientCredentials patientCredentials) {
        var claims = new HashMap<String, Object>();
        claims.put("roles", Collections.singletonList(Role.ROLE_USER));
        return doGenerateToken(claims, String.valueOf(patientCredentials.getPatientNumber()));
    }


    private String doGenerateToken(Map<String, Object> claims, String subject) {
        var expiration = Long.parseLong(expirationTime);
        var expirationDate = Instant.now().plus(Duration.ofSeconds(expiration));
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setExpiration(Date.from(expirationDate))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }

    public String getUsername(String token) {
        return getClaims(token).getSubject();
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        return getRoles(token)
            .stream()
            .map(Role::name)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    private List<Role> getRoles(String token) {
        return ((List<String>) (getClaims(token).get("roles", List.class)))
            .stream()
            .map(Role::valueOf)
            .collect(Collectors.toList());
    }


    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDate(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}
