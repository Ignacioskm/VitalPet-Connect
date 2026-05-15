package com.vitalpet.msauth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //Aca vamos a definir como se protegerá la API REST

    //Por lo que entendí bean, lo que hace es que una vez creado el objeto se lo da a spring para que lo administre él.
    @Bean
    public PasswordEncoder passwordEncoder(){ //Interfaz de spring security tiene marches y encode
        return new BCryptPasswordEncoder(); //Impl de algoritmo BCrypt
    }


    //Aca va a ir el filtro que queremos para nuestro entorno
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf(AbstractHttpConfigurer::disable) //Desactivamos CSRF porque no usamos sesiones de navegador tradicionales
                //STATELESS declara la sesión sin estado, significa que el servidor no recordará quien eres entre una petición y otra.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register","/api/auth/login","/error").permitAll() //la dejamos como rutas publicas
                        .anyRequest().authenticated() //el resto requiere toquen si o si
                );
        return http.build();
    }

}
