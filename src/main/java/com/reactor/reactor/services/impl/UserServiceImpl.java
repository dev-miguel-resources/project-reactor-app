package com.reactor.reactor.services.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.reactor.reactor.models.Role;
import com.reactor.reactor.models.User;
import com.reactor.reactor.repositories.IGenericRepo;
import com.reactor.reactor.repositories.IRoleRepo;
import com.reactor.reactor.repositories.IUserRepo;
import com.reactor.reactor.services.IUserService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends CRUDImpl<User, String> implements IUserService {

    private final IUserRepo userRepo;

    private final IRoleRepo roleRepo;

    private final BCryptPasswordEncoder bcrypt;

    @Override
    protected IGenericRepo<User, String> getRepo() {
        return userRepo;
    }

    @Override
    public Mono<User> saveHash(User user) {
        user.setPassword(bcrypt.encode(user.getPassword())); // Encripta contraseñas
        return userRepo.save(user); // Guarda el usuario en la bdd
    }

    /*
     * Busca un usuario por su nombre de usuario y lo convierte a un objeto
     * compatible con Spring Security
     * Buscar al usuario en la bdd, por cada rol asociado al usuario, obtener el
     * nombre
     * del rol desde el repo de Role.
     * Construir una lista de roles como Strings y luego crear un objeto de Spring
     * Security
     * (com.reactor.reactor.security.User) con: username, password encriptado,
     * estado, lista de roles
     * Esto permite que el AuthenticationManager y JWTUtil puedan autenticar y
     * generar
     * tokens correctamente.
     */
    @Override
    public Mono<com.reactor.reactor.security.User> searchByUser(String username) {
        return userRepo.findOneByUsername(username) // Busque el usuario en la bdd
                // Iterar sobre los roles del usuario
                .flatMap(user -> Flux.fromIterable(user.getRoles())
                        .flatMap(userRole -> roleRepo.findById(userRole.getId()) // Buscar cada rol en el repo
                                .map(Role::getName))
                        .collectList() // convierte el flujo de nombres de roles en una lista
                        .map(roles -> new com.reactor.reactor.security.User(
                                user.getUsername(),
                                user.getPassword(),
                                user.isStatus(),
                                roles)));

    }

}
