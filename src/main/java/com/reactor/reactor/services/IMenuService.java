package com.reactor.reactor.services;

import com.reactor.reactor.models.Menu;

import reactor.core.publisher.Flux;

public interface IMenuService extends ICRUD<Menu, String> {

    Flux<Menu> getMenus(String[] roles);
}
