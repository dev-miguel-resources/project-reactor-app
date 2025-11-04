package com.reactor.reactor.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.reactor.reactor.models.Menu;
import com.reactor.reactor.repositories.IGenericRepo;
import com.reactor.reactor.repositories.IMenuRepo;
import com.reactor.reactor.services.IMenuService;

import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends CRUDImpl<Menu, String> implements IMenuService {

    private final IMenuRepo repo;

    @Override
    protected IGenericRepo<Menu, String> getRepo() {
        return repo;
    }

    @Override
    public Flux<Menu> getMenus(String[] roles) {
        return repo.getMenus(roles);
    }
}
