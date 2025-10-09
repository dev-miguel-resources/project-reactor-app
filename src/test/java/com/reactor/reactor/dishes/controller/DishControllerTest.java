package com.reactor.reactor.dishes.controller;

import static org.mockito.ArgumentMatchers.any;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactor.reactor.controllers.DishController;
import com.reactor.reactor.dtos.DishDTO;
import com.reactor.reactor.models.Dish;
import com.reactor.reactor.services.IDishService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = DishController.class)
public class DishControllerTest {

    @Autowired
    private WebTestClient client;

    // @MockBean
    @MockitoBean
    private IDishService service;

    // @MockBean
    @MockitoBean
    @Qualifier("defaultMapper")
    private ModelMapper modelMapper;

    // Objetos que se utilizarán como data para las pruebas
    private Dish dish1;
    private Dish dish2;
    private DishDTO dish1DTO;
    private DishDTO dish2DTO;
    private List<Dish> dishes;

    // Mock para el contexto web necesario para procesar los endpoints y excepciones
    // en un contexto flux simulado.
    // @MockBean -> versiones spring 3.3.4 hacia abajo
    // @MockitoBean -> 3.5.x en adelante
    @MockitoBean
    private Resources resources;

    // Método que se ejecuta antes de cada test.
    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this); // Inicializar todos los mocks anotados en esta clase.

        // Crear 2 objetos Dish (entidades de dominio).
        dish1 = new Dish("1", "Soda", 10.2, true);
        dish2 = new Dish("2", "Pizza", 29.9, true);

        // Crear sus equivalencias a nivel de DTO para las respuestas HTTP.
        dish1DTO = new DishDTO("1", "Soda", 10.2, true);
        dish2DTO = new DishDTO("2", "Pizza", 29.9, true);

        // Crear una lista de platos.
        dishes = new ArrayList<>();
        dishes.add(dish1);
        dishes.add(dish2);

        // Definir el comportamiento simulado de todos los métodos del servicio y el
        // mapper. (contexto)
        Mockito.when(service.findAll()).thenReturn(Flux.fromIterable(dishes));
        Mockito.when(modelMapper.map(dish1, DishDTO.class)).thenReturn(dish1DTO);
        Mockito.when(modelMapper.map(dish2, DishDTO.class)).thenReturn(dish2DTO);
        Mockito.when(service.save(any())).thenReturn(Mono.just(dish1));
        Mockito.when(service.update(any(), any())).thenReturn(Mono.just(dish1));
    }

    @Test // Prueba el endpoint GET /dishes (lista todos los platos).
    public void findAllTest() {
        client.get()
                .uri("/dishes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // simula el envío de la solicitud
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }

    @Test // Prueba el endpoint POST /dishes (insertar un plato).
    public void saveTest() {
        client.post()
                .uri("/dishes")
                .body(Mono.just(dish1DTO), DishDTO.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // simula el envío de la solicitud
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nameDish").isNotEmpty()
                .jsonPath("$.priceDish").isNumber()
                .jsonPath("$.statusDish").isBoolean();
    }

    @Test // Prueba el endpoint PUT /dishes/{id} (actualizar un plato).
    public void updateTest() {
        client.put()
                .uri("/dishes/" + dish1DTO.getId())
                .body(Mono.just(dish1DTO), DishDTO.class)
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // simula el envío de la solicitud
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.nameDish").isNotEmpty()
                .jsonPath("$.priceDish").isNumber()
                .jsonPath("$.statusDish").isBoolean();
    }

    @Test
    public void deleteTest() {
        Mockito.when(service.delete(any())).thenReturn(Mono.just(true));

        client.delete()
                .uri("/dishes/" + dish1DTO.getId())
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void deleteTestFalse() {
        Mockito.when(service.delete(any())).thenReturn(Mono.just(false));

        client.delete()
                .uri("/dishes/" + dish1DTO.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

}
