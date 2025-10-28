package com.reactor.reactor.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/* 
 * Clase que representa a un usuario dentro del sistema de seguridad de Spring Security.
 * Implementa la interfaz UserDetails, que define los métodos requeridos por el framework
 * para manejar la autenticación y autorización.
*/

// Clase 1
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    // Nombre de usuario utilizado para la autenticación
    private String username;

    // Contraseña del usuario. Se ignora al serializar en JSON para proteger la
    // información sensible
    @JsonIgnore
    private String password;

    // Indicar si el usuario está habilitado (activo) dentro del sistema.
    private boolean enabled;

    // Lista de roles/perfiles asociados al usuario. Por ej: "ROLE_ADMIN" &
    // "ROLE_USER"
    private List<String> roles;

    /*
     * Devolver la colección de autoridades (roles) del usuario.
     * Spring Security utiliza este método para verificar los permisos.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertir cada String de la lista de roles en un objeto
        // SimpleGrantedAuthority
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    /*
     * Indica si la cuenta del usuario no ha expirado.
     * En este caso, retornamos false, lo que implica que el control de expiración
     * no está implementado.
     */
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    /*
     * Indica si la cuenta no está bloqueada.
     * En este caso, retornamos false para desactivar el control de bloqueo
     * automático.
     */
    public boolean isAccountNonLocked() {
        return false;
    }

    /*
     * Indica si las credenciales no han expirado.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    /*
     * Indica si el usuario está habilitado.
     * Este valor toda la definición del campo "enabled"
     */
    public boolean isEnabled() {
        return this.enabled;
    }

}
