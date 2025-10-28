package com.reactor.reactor.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Clase 2
/*
 * Esta clase representa el modelo de datos que se recibe cuando un usuario intenta autenticarse.
 * Esto va esta relacionada al cuerpo de datos asociado al inicio de sesión.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {

    // Campo que almacena el nombre del usuario que el cliente envía en el login
    private String username;

    // Campo que almacena la contraseña asociada al usuario.
    private String password;

}
