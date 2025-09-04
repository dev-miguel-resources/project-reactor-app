package com.reactor.reactor.controllers;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

import org.cloudinary.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.reactor.reactor.dtos.ClientDTO;
import com.reactor.reactor.models.Client;
import com.reactor.reactor.paginations.PageSupport;
import com.reactor.reactor.services.IClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    private final IClientService service;

    private final Cloudinary cloudinary;

    @Qualifier("clientMapper")
    private final ModelMapper modelMapper;

    private ClientDTO convertToDto(Client model) {
        return modelMapper.map(model, ClientDTO.class);
    }

    private Client convertToDocument(ClientDTO dto) {
        return modelMapper.map(dto, Client.class);
    }

    @GetMapping()
    public Mono<ResponseEntity<Flux<ClientDTO>>> findAll() {
        Flux<ClientDTO> fx = service.findAll().map(e -> convertToDto(e));
        // Flux<ClientDTO> fx = service.findAll().map(this::convertToDto);

        return fx.hasElements()
                .flatMap(hasElements -> {
                    if (hasElements) {
                        return Mono.just(ResponseEntity.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(fx));
                    } else {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                });
    }

    @PostMapping
    public Mono<ResponseEntity<ClientDTO>> save(@Valid @RequestBody ClientDTO dto, final ServerHttpRequest req) {
        return service.save(convertToDocument(dto))
                // .map(e -> convertToDto(e))
                .map(this::convertToDto)
                .map(e -> ResponseEntity.created(
                        URI.create(req.getURI().toString().concat("/").concat(e.getId())))
                        .contentType(
                                MediaType.APPLICATION_JSON)
                        .body(e));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<ClientDTO>> findById(@PathVariable("id") String id) {
        return service.findById(id)
                .map(this::convertToDto)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ClientDTO>> update(@Valid @PathVariable("id") String id, @RequestBody ClientDTO dto) {
        return Mono.just(dto)
                .map(e -> {
                    e.setId(id);
                    return e;
                })
                .flatMap(e -> service.update(id, convertToDocument(dto)))
                .map(this::convertToDto)
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .doOnNext(e -> log.info("Element: " + e))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable("id") String id) {
        return service.delete(id)
                .flatMap(result -> { // Mono<Boolean>
                    if (result) {
                        return Mono.just(ResponseEntity.noContent().build());
                    } else {
                        return Mono.just(ResponseEntity.notFound().build());
                    }
                });
    }

    @GetMapping("/pageable")
    public Mono<ResponseEntity<PageSupport<ClientDTO>>> getPage(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "2") int size) {

        // Se llama al servicio para obtener una página de datos usando los valores de
        // "page" y "size"
        return service.getPage(PageRequest.of(page, size))

                // Transformar el resultado de PageSupport<T> original a uno con ClientDTO,
                // usando
                // el mapper
                .map(pageSupport -> new PageSupport<>(
                        pageSupport.getContent().stream().map(this::convertToDto).toList(),

                        pageSupport.getPageNumber(), // Número de la página actual
                        pageSupport.getPageSize(), // Tamaño de la página
                        pageSupport.getTotalElements() // Total de elementos
                ))

                // Empaquetamos el resultado dentro de un ResponseEntity en formato JSON
                .map(e -> ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(e))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    // Versión simple + detallado
    // MultipartFile: apps no reactivas
    // FilePart: enfoque reactivo
    @PostMapping("/v1/upload/{id}")
    public Mono<ResponseEntity<ClientDTO>> uploadV1(@PathVariable("id") String id,
            @RequestPart("file") FilePart filePart) {

        // Buscar al cliente por su id
        return service.findById(id)
                .flatMap(client -> { // cuando lo encuentra, ejecuta un lambda function
                    try {
                        // 1. Crear una referencia temporal con el mismo nombre que el archivo subido
                        File f = Files.createTempFile("temp", filePart.filename()).toFile();

                        // 2. Transferir el contenido del archivo recibido (FilePart) al recurso
                        // temporal
                        filePart.transferTo(f).block();

                        // 3. Subir el archivo a Cloudinary
                        @SuppressWarnings("unchecked")
                        Map<String, Object> response = cloudinary.uploader().upload(f,
                                ObjectUtils.asMap("resource_type", "auto"));

                        // 4. Extraer la URL de la imagen subida a cloudinary desde la respuesta del
                        // JSON
                        JSONObject json = new JSONObject(response);
                        String url = json.getString("url");

                        // 5. Actualizar el cliente con la nueva URL de la foto
                        client.setUrlPhoto(url);

                        // 6. Guardar los cambios en la bdd, convertir a DTO y retornan el
                        // ResponseEntity
                        return service.update(id, client)
                                .map(this::convertToDto)
                                .map(e -> ResponseEntity.ok().body(e));

                    } catch (Exception e) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                });

    }

    // Versión: más técnica, moderno y de mayor performance
    @PostMapping("/v2/upload/{id}")
    public Mono<ResponseEntity<ClientDTO>> uploadV2(@PathVariable("id") String id,
            @RequestPart("file") FilePart filePart) {

        return Mono.fromCallable(() -> {
            // 1. Crear una referencia temporal con el mismo nombre que el archivo subido
            return Files.createTempFile("temp", filePart.filename()).toFile();
        })
                .flatMap(tempFile ->
                // 2. Transferir el contenido del archivo recibido (FilePart) al recurso
                // temporal
                filePart.transferTo(tempFile)
                        .then(service.findById(id) // 3. Buscar al cliente
                                .flatMap(client -> {
                                    // Crear nuevamente un mono a partir e una función
                                    return Mono.fromCallable(() -> {
                                        // 4. Subir el archivo a Cloudinary
                                        @SuppressWarnings("unchecked")
                                        Map<String, Object> response = cloudinary.uploader().upload(tempFile,
                                                ObjectUtils.asMap("resource_type", "auto"));

                                        // 5. Obtener la URL de la imagen subida
                                        JSONObject json = new JSONObject(response);
                                        String url = json.getString("url");

                                        // 6. Actualizar el cliente con la photo url
                                        client.setUrlPhoto(url);

                                        // 7. Guardar los cambios en la bdd, convertir a DTO y retornan el
                                        // ResponseEntity
                                        return service.update(id, client)
                                                .map(this::convertToDto)
                                                .map(e -> ResponseEntity.ok().body(e));
                                        // Hasta aquí tenemos: Mono<Mono<ResponseEntity<ClientDTO>>>
                                    }).flatMap(mono -> mono);
                                })));
    }

}
