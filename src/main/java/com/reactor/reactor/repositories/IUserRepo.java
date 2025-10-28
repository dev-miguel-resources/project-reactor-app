package com.reactor.reactor.repositories;

import com.reactor.reactor.models.User;

import reactor.core.publisher.Mono;

public interface IUserRepo extends IGenericRepo<User, String> {

    Mono<User> findOneByUsername(String username);

}
