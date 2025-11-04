package com.reactor.reactor.repositories;

import org.springframework.data.mongodb.repository.Query;

import com.reactor.reactor.models.Menu;

import reactor.core.publisher.Flux;

public interface IMenuRepo extends IGenericRepo<Menu, String> {

    @Query("{ 'roles' : { $in:  ?0} }")
    Flux<Menu> getMenus(String[] roles);

}
