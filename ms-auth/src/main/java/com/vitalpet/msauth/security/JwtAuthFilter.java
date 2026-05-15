package com.vitalpet.msauth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extraer el header de autorización
        String authHeader = request.getHeader("Authorization");

        // 2. Verificar que venga el token y empiece con "Bearer"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Cortamos la palabra "Bearer"

            // 3. Validar si el token es matemáticamente correcto y no ha expirado
            if (!jwtProvider.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token invalido o expirado");
                return; // Cortamos el paso aquí mismo
            }
        }

        // Si todo está bien, o si la ruta es pública y no traía token, dejamos que siga su camino
        filterChain.doFilter(request, response);
    }
}
