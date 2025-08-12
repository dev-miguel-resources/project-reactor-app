package com.reactor.reactor.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactor.reactor.dtos.DishDTO;
import com.reactor.reactor.models.Dish;
import com.reactor.reactor.services.IDishService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/dishes")
@RequiredArgsConstructor
public class DishController {

    private final IDishService service;

    @Qualifier("defaultMapper")
    private final ModelMapper modelMapper;

    private DishDTO convertToDto(Dish model) {
        return modelMapper.map(model, DishDTO.class);
    }

    private Dish convertToDocument(DishDTO dto) {
        return modelMapper.map(dto, Dish.class);
    }

    @GetMapping()
    public Mono<ResponseEntity<Flux<DishDTO>>> findAll() {
        Flux<DishDTO> fx = service.findAll().map(e -> convertToDto(e));
        // Flux<DishDTO> fx = service.findAll().map(this::convertToDto);

        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fx)).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /*
     * @PostMapping
     * public Mono<ResponseEntity<DishDTO>> save() {
     * 
     * }
     * 
     * @GetMapping("/{id}")
     * public Mono<ResponseEntity<DishDTO>> findById() {
     * 
     * }
     * 
     * @PutMapping("/{id}")
     * public Mono<ResponseEntity<DishDTO>> update() {
     * 
     * }
     * 
     * @DeleteMapping("/{id}")
     * public Mono<ResponseEntity<Void>> delete() {
     * 
     * }
     */

}
