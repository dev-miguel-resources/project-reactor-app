package com.reactor.reactor.dishes.service;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.modelmapper.internal.util.Assert;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.reactor.reactor.models.Dish;
import com.reactor.reactor.repositories.IDishRepo;

import reactor.core.publisher.Flux;

@ExtendWith(SpringExtension.class)
public class DishServiceTest {

    @MockitoBean
    private IDishRepo repo;

    @Test
    public void testFindAll() {

        // Probar el flujo de datos de los platos sin necesidad de conectarnos a la bdd
        // de manera real.
        Mockito.when(repo.findAll()).thenReturn(Flux.fromIterable(Arrays.asList(new Dish(), new Dish())));

        // Verificación: usar JAssert para asegurarnos que la llamada al "findAll" no
        // devuelva nulo.
        // Esto válida que la configuración simulada del mock funciona correctamente y
        // que el
        // repositorio devuelve un resultado reactivo válido.
        // Assert.notNull(repo.findAll());
        Assert.notNull(repo.findAll());

    }

}
