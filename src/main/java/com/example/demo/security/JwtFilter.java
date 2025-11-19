package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 1. RUTAS ESTÁTICAS Y PÚBLICAS → nada que hacer
        if (path.startsWith("/auth/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.contains(".") ||
                "/favicon.ico".equals(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 2. RUTAS WEB PROTEGIDAS → usamos la sesión que creamos en login
        if (path.startsWith("/home") || path.startsWith("/home/") ||
                path.startsWith("/clientes") || path.startsWith("/clientes/") ||
                path.startsWith("/reclamos") || path.startsWith("/reclamos/") ||
                path.startsWith("/reportes") || path.startsWith("/reportes/")) {

            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("token") != null) {
                // Creamos autenticación para que Spring Security esté feliz
                String username = (String) session.getAttribute("nombreUsuario");
                var auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        AuthorityUtils.createAuthorityList("ROLE_USER")
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            chain.doFilter(request, response);
            return;
        }

        // 3. API → JWT clásico
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token requerido");
            return;
        }

        String token = header.substring(7);
        if (!jwtService.esValido(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
            return;
        }

        String username = jwtService.obtenerUsername(token);
        Set<String> roles = jwtService.obtenerRoles(token);

        var authorities = roles.stream()
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .toList();

        var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}
