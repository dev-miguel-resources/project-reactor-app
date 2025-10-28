package com.reactor.reactor.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

// Clase 6
/*
 * Esta clase se encarga de recuperar el contexto de seguridad 
 * a partir del token JWT enviado por el cliente en cada petición HTTP.
 * Gestionar el contexto de seguridad de manera reactiva.
 */
@Component
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {

    // Necesitamos traernos el authenticator encargado y responsable de validar el
    // token de JWT
    private final AuthenticationManager authenticationManager;

    // Método no utilizado para manejo de sesiones con estado, implementación ya más
    // legacy
    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return null;
    }

    /*
     * Método principal que carga el contexto de seguridad a partir de solicitudes
     * HTTP.
     * Se ejecuta en cada petición para validar si el cliente envía un token válido,
     * y si es así,
     * construir el contexto de seguridad asociado al usuario autenticado.
     */
    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {

        // Obtener el objeto ServerHttpRequest
        ServerHttpRequest request = exchange.getRequest();

        // Extraer los headers HTTP "Authorization", donde debería venir el token JWT
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Verificar si el encabezado está presente y si comienza con el prefijo
        // "Bearer"
        if (authHeader == null || !(authHeader.startsWith("Bearer ") || authHeader.startsWith("bearer "))) {
            // Si no cumple las condiciones, se lanza un error 401 (No authenticated)
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not autenticated"));
        }

        // Posición 1 del arreglo ["Bearer", "token"]
        final int TOKEN_POSITION = 1;

        // Dividir el header por el espacio y obtener el token JWT propiamente tal.
        // Ejemplo: "Bearer jasosjaosajsoajsaoasj"
        String token = authHeader.split(" ")[TOKEN_POSITION];

        // Crear un objeto Authentication temporal usando el token como referencia del
        // usuario y contraseña.
        Authentication auth = new UsernamePasswordAuthenticationToken(token, token);

        // Si el token es válido, se construye un SecurityContext con la información del
        // usuario autenticado.
        // Si el token es válido pasa a la definición del AuthenticationManager.
        // Devuelve el usuario autenticado.
        return this.authenticationManager.authenticate(auth)
                .map(SecurityContextImpl::new); // Envuelve el resultado en un contexto válido de seguridad para dicho
                                                // usuario.

    }

}
