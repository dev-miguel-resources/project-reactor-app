package com.reactor.reactor.security;

import java.util.Date;

// Clase 3
/* 
 * Esta clase repreenta la respuesa que se envía al cliente después de una autenticación exitosa.
 * Ideal para transportar datos de manera simple y segura, sin necesidad de getters, setters o constructores.
*/
public record AuthReponse(
        String token, // Token de JWT generado y firmado para el usuario autenticado.
        Date expiration) { // Fecha exacta en la que el token expirará (definido en su generación).

}
