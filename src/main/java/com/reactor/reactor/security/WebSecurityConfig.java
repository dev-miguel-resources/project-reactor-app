package com.reactor.reactor.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import lombok.RequiredArgsConstructor;

// Clase 7
/*
 * Esta clase reemplaza al clásico WebSecurityConfigurerAdapter (usado en apps no reactivas).
 * Define como se autentican los usuarios, qué rutas requieren permisos y como se maneja
 * la seguridad general
 */
@Configuration
@EnableWebFluxSecurity // Habilita la seguridad en apps reactivas basadas en Webflux.
@EnableReactiveMethodSecurity // Permite usar anotaciones famosas como @PreAuthorize
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    /*
     * Bean encargado de encriptar usando el algoritmo Bcript como método seguro
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * Bean principal de configuración de la cadena de filtros de seguridad
     * reactiva.
     * Define que rutas son públicas, cuáles requieren autenticación y como se
     * manejan
     * las soliticitudes HTTP.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // Desde Spring Boot 3.1+, esta es la forma moderna de configurar la seguridad
        // en apps reactivas.
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // Quien valida las credenciales
                .authenticationManager(authenticationManager)
                // Define quien maneja el contexto de seguridad
                // encargado de almacenar y recuperar la información del usuario autenticado
                // mediante JWT
                .securityContextRepository(securityContextRepository)
                // Configurar las reglas de autorización de las diferentes rutas
                .authorizeExchange(req -> {
                    // Permite el acceso de ruteo público
                    req.pathMatchers("/login").permitAll();
                    // req.pathMatchers("/dishes/**").permitAll();
                    // Cualquier otra ruta (no especificada antes) requiere autenticación.
                    req.anyExchange().authenticated();
                })

                // Finalmente, construye y retorna la cadena de filtro de seguridad.
                .build();
    }

}
