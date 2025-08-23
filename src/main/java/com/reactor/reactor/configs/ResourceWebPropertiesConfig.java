package com.reactor.reactor.configs;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ResourceWebPropertiesConfig {

    /*
     * Agregamos la definición de permiso de recursos para la clase manejadora de
     * excepciones
     * Si este bean no existe en memoria las excepciones no podrán interceptar las
     * solicitudes http,
     * al no existir este bean precargado en memoria con los permisos.
     */
    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

}
