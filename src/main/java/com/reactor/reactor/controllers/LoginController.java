package com.reactor.reactor.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.reactor.reactor.security.AuthReponse;
import com.reactor.reactor.security.AuthRequest;
import com.reactor.reactor.security.JwtUtil;
import com.reactor.reactor.services.IUserService;

import reactor.core.publisher.Mono;

import java.util.Date;

/**
 * Controlador REST para manejar la autenticación de usuarios.
 * 
 * Este controlador expone un endpoint `/login` que recibe las credenciales del
 * usuario
 * y, si son válidas, devuelve un token JWT junto con su fecha de expiración.
 * 
 * Se implementa usando **Spring WebFlux** y programación **reactiva**.
 */
@RestController // Marca la clase como un controlador REST, capaz de recibir y responder
                // solicitudes HTTP
@RequiredArgsConstructor // Genera un constructor con todos los campos 'final', para inyección de
                         // dependencias
public class LoginController {

    // Componente responsable de generar y validar tokens JWT
    private final JwtUtil jwtUtil;

    // Servicio que maneja la búsqueda y persistencia de usuarios
    private final IUserService service;

    /**
     * Endpoint POST para iniciar sesión.
     * 
     * Recibe un objeto AuthRequest con las credenciales del usuario,
     * valida el password, y devuelve un AuthResponse con el token JWT si las
     * credenciales son correctas.
     *
     * @param authRequest Objeto que contiene username y password
     * @return Mono<ResponseEntity<?>> operación reactiva que emite la respuesta
     *         HTTP
     */
    @PostMapping("/login")
    public Mono<ResponseEntity<?>> login(@RequestBody AuthRequest authRequest) {
        // Busca el usuario en la base de datos (reactivo)
        return service.searchByUser(authRequest.getUsername())
                .map(userDetails -> {
                    // Verifica que la contraseña proporcionada coincida con el hash almacenado
                    if (BCrypt.checkpw(authRequest.getPassword(), userDetails.getPassword())) {
                        // Genera un token JWT para el usuario autenticado
                        String token = jwtUtil.generateToken(userDetails);

                        // Obtiene la fecha de expiración del token
                        Date expiration = jwtUtil.getExpirationDateFromToken(token);

                        // Devuelve un ResponseEntity con status 200 OK y el token dentro de
                        // AuthResponse
                        return ResponseEntity.ok(new AuthReponse(token, expiration));
                    } else {
                        // Si la contraseña no coincide, devuelve status 401 Unauthorized
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                })
                // Si no se encuentra el usuario, también devuelve 401 Unauthorized
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }
}
