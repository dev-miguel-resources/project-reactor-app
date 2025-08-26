package com.reactor.reactor.services.impl;

import org.springframework.data.domain.Pageable;

import com.reactor.reactor.paginations.PageSupport;
import com.reactor.reactor.repositories.IGenericRepo;
import com.reactor.reactor.services.ICRUD;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class CRUDImpl<T, ID> implements ICRUD<T, ID> {

    protected abstract IGenericRepo<T, ID> getRepo();

    @Override
    public Mono<T> save(T t) {
        return getRepo().save(t);
    }

    @Override
    public Mono<T> update(ID id, T t) {
        return getRepo().findById(id).flatMap(e -> getRepo().save(t));
    }

    @Override
    public Flux<T> findAll() {
        return getRepo().findAll();
    }

    @Override
    public Mono<T> findById(ID id) {
        return getRepo().findById(id);
    }

    @Override
    public Mono<Boolean> delete(ID id) {
        return getRepo().findById(id)
                .hasElement()
                .flatMap(result -> {
                    if (result) {
                        return getRepo().deleteById(id).thenReturn(true);
                    } else {
                        return Mono.just(false);
                    }
                });
    }

    @Override
    public Mono<PageSupport<T>> getPage(Pageable pageable) {
        return getRepo().findAll() // Obtenemos todos los elementos del Flux<T>
                .collectList() // Convertimos el Flux en una lista completa (Mono<List<T>>)
                .map(list -> new PageSupport<>( // Mapea la lista completa a un objeto PageSupport
                        // Ej de list: [1,2,3,4,5,6,7,8,9,10]
                        // Suponiendo: pageNumber = 0, pageSize = 2
                        list.stream()
                                // Salta los elementos anteriores a la página solicitada
                                // skip(0 * 2) = skip(0), no se salta nada para la primera página
                                .skip(pageable.getPageNumber() * pageable.getPageSize())
                                // Limita la cantidad de elementos al tamaño de la página (pageSize)
                                // limit (2), toma 2 elementos
                                .limit(pageable.getPageSize())
                                // Convierte el stream de elementos paginas a una lista
                                .toList(),
                        // Número de página actual (por ej: 0)
                        pageable.getPageNumber(),
                        // Tamaño de página solicitado (por ej: 2)
                        pageable.getPageSize(),
                        // Total de elementos encontrados (por ej: 10)
                        list.size()));
    }

}
