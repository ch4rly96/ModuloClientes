package com.example.demo.config;

import com.example.demo.security.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth

                        // 1. PRIMERO: RUTAS WEB PÚBLICAS
                        .requestMatchers("/auth/**", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                        .requestMatchers("/dashboard/content", "/fragments/**", "/api/user/name").permitAll()

                        // 2. SEGUNDO: RUTAS WEB PROTEGIDAS (solo usuarios autenticados con sesión)
                        .requestMatchers("/home", "/home/**", "/clientes", "/clientes/**", "/reclamos", "/reclamos/**", "/reportes", "/reportes/**")
                        .authenticated()

                        // 3. TERCERO: API AUTH (permitido para todos)
                        .requestMatchers("/api/auth/**").permitAll()

                        // 4. CUARTO: API CLIENTES POR MÉTODO (con roles)
                        .requestMatchers(HttpMethod.GET, "/api/clientes/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.POST, "/api/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/clientes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasRole("ADMIN")

                        // 5. OTROS ENDPOINTS API
                        .requestMatchers("/reclamos/**").hasAnyRole("ADMIN", "CAJERO", "SUPERVISOR")

                        // 6. ÚLTIMO: TODO LO DEMÁS
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}