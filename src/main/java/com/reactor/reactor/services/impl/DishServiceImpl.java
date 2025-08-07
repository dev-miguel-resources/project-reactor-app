package com.reactor.reactor.services.impl;

import org.springframework.stereotype.Service;

import com.reactor.reactor.models.Dish;
import com.reactor.reactor.repositories.IDishRepo;
import com.reactor.reactor.repositories.IGenericRepo;
import com.reactor.reactor.services.IDishService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DishServiceImpl extends CRUDImpl<Dish, String> implements IDishService {

    private final IDishRepo repo;

    @Override
    protected IGenericRepo<Dish, String> getRepo() {
        return repo;
    }

}
