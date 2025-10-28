package com.reactor.reactor.services;

import com.reactor.reactor.models.User;

import reactor.core.publisher.Mono;

public interface IUserService extends ICRUD<User, String> {

    Mono<User> saveHash(User user);

    Mono<com.reactor.reactor.security.User> searchByUser(String username);

}
