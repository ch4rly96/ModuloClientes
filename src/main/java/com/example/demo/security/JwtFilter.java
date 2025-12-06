package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
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
        String method = request.getMethod();

        // ---- RUTAS TOTALMENTE PÚBLICAS ----
        if (path.equals("/") ||
                path.startsWith("/auth/") ||
                path.startsWith("/css/") ||
                path.startsWith("/js/") ||
                path.startsWith("/images/") ||
                path.startsWith("/layout/") ||
                path.startsWith("/fragments/") ||
                "/favicon.ico".equals(path)) {

            chain.doFilter(request, response);
            return;
        }

        // ---- LOGOUT  ----
        if (path.equals("/auth/logout")) {
            chain.doFilter(request, response);
            return;
        }

        // ---- RUTAS WEB PROTEGIDAS POR SESIÓN ----
        if (path.startsWith("/home") ||
                path.startsWith("/clientes") ||
                path.startsWith("/reclamos") ||
                path.startsWith("/reportes") ||
                path.startsWith("/fidelizacion")) {

            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("token") == null) {
                response.sendRedirect("/auth/login");
                return;
            }

            String username = (String) session.getAttribute("nombreUsuario");
            Set<String> roles = (Set<String>) session.getAttribute("roles");

            List<GrantedAuthority> authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .map(GrantedAuthority.class::cast)
                    .toList();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            authorities
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request, response);
            return;
        }

        // ---- API (JWT Obligatorio) ----
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

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }
}