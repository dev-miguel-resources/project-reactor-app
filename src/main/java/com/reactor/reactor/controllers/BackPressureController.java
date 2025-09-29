package com.reactor.reactor.controllers;

import java.time.Duration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactor.reactor.models.Dish;
import com.reactor.reactor.services.IDishService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/backpressure")
@RequiredArgsConstructor
public class BackPressureController {

    private final IDishService service;

    @GetMapping(value = "/json", produces = "application/json")
    public Flux<Dish> json() {
        return Flux.interval(Duration.ofMillis(100))
                .map(t -> new Dish("1", "Soda", 5.90, true));
    }

    @GetMapping(value = "/event", produces = "text/event-stream")
    public Flux<Dish> eventStream() {
        return service.findAll().repeat(10000);
    }

    /*
     * Reseña: Por defecto el algoritmo de backpressure con limitRate utiliza un
     * 75% del hightide
     * Ese 75% es un valor por defecto que usa la concurrencia reactiva para evitar
     * 2 problemas:
     * . 1. Que el flujo de emisión de la respuesta se detenga:
     * Ej: Si se esperara a consumir los 10 elementos antes de pedir más, habría
     * pausa mientras se solicitan y envian los nuevos datos.
     * Con el 75% se rellena el "buffer" antes de que se vacía.
     * Que el consumidor se sature:
     * Ej. Si se pidieran demasiados elementos de golpe, el consumidor podría
     * quedarse sin memoria o tardar demasiado en procesarlos.
     * Con lotes más pequeños y solicitudes progresivas, se mantiene con esto un
     * flujo estable y continuo ante escenarios de intensivo procesamiento.
     */
    @GetMapping("/limitRate")
    public Flux<Integer> limitRate() {
        return Flux.range(1, 5000000)
                .log()
                // hightide (ola alta)
                // lowtide (ola baja)
                .limitRate(10, 8)
                .delayElements(Duration.ofMillis(1));
    }

}
