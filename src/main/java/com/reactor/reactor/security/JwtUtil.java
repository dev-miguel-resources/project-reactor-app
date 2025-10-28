package com.reactor.reactor.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

// Clase 4
/*
 * Esta clase se encarga de generar, leer y validar tokens de JWT.
 */
@Component
public class JwtUtil implements Serializable {

    // Duración del token: 5 horas (en milisegundos)
    public final long JWT_TOKEN_VALIDITY = 5 * 60 * 60 * 1000;

    // Clave secreta para firmar los tokens
    @Value("${jjwt.secret}")
    private String secret;

    /*
     * Generar un token JWT a partir de un objeto User.
     * Objeto que contiene la información del usuario autenticado.
     */
    public String generateToken(User user) {
        // Se le da definición con la data (payload)
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles()); // Roles del usuario
        claims.put("username", user.getUsername()); // Nombre de usuario
        claims.put("test-value", "sample");

        // Se retorna el token generado como cadena
        return doGenerateToken(claims, user.getUsername());
    }

    /*
     * Se contruye el token JWT utilizando la librería.
     * Necesito el mapa de claims con la información y el usuario respectivo.
     * Retornar: El token JWT firmado y listo para ser enviado al cliente.
     */
    private String doGenerateToken(Map<String, Object> claims, String username) {
        // Se genera una clave de firma a partir del secreto configurado.
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes());

        // Se construye el token
        return Jwts.builder()
                .claims(claims) // Datos adicionales asociados al payload
                .subject(username) // Identifica al usuario dueño del token
                .issuedAt(new Date(System.currentTimeMillis())) // Fecha de creación
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)) // fecha de expiración
                .signWith(key) // Firma con la clave secreta
                .compact(); // Devuelve el token en formato compacto (String)

    }

    // Métodos para la validación y lectura de los tokens

    /*
     * Obtener todos los claims (datos del payload) de un token.
     * Retorna un Claims con los datos internos del token.
     */
    public Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes());

        // Se construye el parser con la clave secreta para verificar la firma del token
        return Jwts.parser()
                .verifyWith(key) // Verificar la firma
                .build()
                .parseSignedClaims(token) // Parsea el token y valida su estructura
                .getPayload(); // Retornar el cuerpo (payload) con los datos
    }

    /*
     * Extrae el nombre de usuario (subject) del token
     */
    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    /*
     * Obtiene la fecha de expiración de un token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    /*
     * Verificar si un token ha expirado.
     */
    private boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        // Si la fecha de expiración es anterior a la actual, está vencido.
        return expiration.before(new Date());
    }

    /*
     * Validar un token de JWT
     * Retornar true si el token es válido y no está expirado, false en caso de lo
     * contrario.
     */
    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

}
