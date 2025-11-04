package com.reactor.reactor.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Clase S7 - Configuración de seguridad principal para aplicaciones reactivas
 * con Spring WebFlux.
 * 
 * Esta clase reemplaza al clásico WebSecurityConfigurerAdapter (usado en
 * aplicaciones no reactivas).
 * Define cómo se autentican los usuarios, qué rutas requieren permisos y cómo
 * se maneja la seguridad general.
 */
@Configuration // Indica que esta clase forma parte de la configuración del contexto de Spring
@EnableWebFluxSecurity // Habilita la seguridad en aplicaciones reactivas basadas en WebFlux
@EnableReactiveMethodSecurity // Permite usar anotaciones como @PreAuthorize o @Secured en los métodos
@RequiredArgsConstructor // Genera automáticamente un constructor con las dependencias marcadas como
                         // 'final'
public class WebSecurityConfig {

    // Dependencias inyectadas por constructor (gracias a @RequiredArgsConstructor)
    // Se utilizan para personalizar la autenticación y la gestión del contexto de
    // seguridad.
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    /**
     * Bean encargado de encriptar contraseñas usando el algoritmo BCrypt.
     * BCrypt es un método seguro y adaptativo, recomendado para almacenar
     * contraseñas.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean principal de configuración de la cadena de filtros de seguridad
     * reactiva.
     * Define qué rutas son públicas, cuáles requieren autenticación y cómo se
     * manejan las solicitudes HTTP.
     */
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // Desde Spring Boot 3.1+, esta es la forma moderna de configurar la seguridad
        // en aplicaciones WebFlux
        return http
                // Desactiva la protección CSRF (Cross-Site Request Forgery)
                // ya que en APIs REST con autenticación basada en tokens (JWT, por ejemplo)
                // no se utilizan cookies de sesión que requieran esta protección.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Desactiva el inicio de sesión con formulario HTML clásico,
                // ya que en entornos reactivos o APIs modernas se suele usar autenticación por
                // token.
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                // Define el manejador de autenticación personalizado.
                // Este se encarga de validar las credenciales del usuario.
                .authenticationManager(authenticationManager)

                // Define el repositorio del contexto de seguridad,
                // encargado de almacenar y recuperar la información del usuario autenticado
                // (por ejemplo, a partir de un token JWT en cada petición).
                .securityContextRepository(securityContextRepository)

                // Configura las reglas de autorización para las distintas rutas.
                .authorizeExchange(req -> {
                    // Permite el acceso público al endpoint /login
                    // (generalmente usado para obtener un token o iniciar sesión).
                    req.pathMatchers("/login").permitAll();

                    // Ejemplo comentado: podría requerir autenticación para ciertas rutas
                    // específicas.
                    // req.pathMatchers("/dishes/**").authenticated();

                    // Cualquier otra ruta (no especificada antes) requiere autenticación.
                    req.anyExchange().authenticated();
                })

                // Finalmente, construye y retorna la cadena de filtros de seguridad.
                .build();
    }
}