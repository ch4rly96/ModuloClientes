package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10 horas

    public String generarToken(String username, Set<String> roles) {
        Set<String> rolesMayus = roles.stream()
                .map(String::toUpperCase)  // ← AQUÍ EL FIX
                .collect(Collectors.toSet());

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", rolesMayus)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    public String obtenerUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Set<String> obtenerRoles(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            List<String> roles = claims.get("roles", List.class);
            return roles != null
                    ? roles.stream().collect(Collectors.toSet())
                    : Set.of();
        } catch (Exception e) {
            return Set.of();
        }
    }

    public boolean esValido(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
