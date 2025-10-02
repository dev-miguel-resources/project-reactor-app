package com.reactor.reactor.dishes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactor.reactor.controllers.DishController;
import com.reactor.reactor.services.IDishService;

@WebFluxTest(controllers = DishController.class)
public class DishControllerTest {

    @Autowired
    private WebTestClient client;

    @MockitoBean
    private IDishService service;

}
