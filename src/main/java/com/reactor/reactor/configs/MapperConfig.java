package com.reactor.reactor.configs;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean(name = "defaultMapper")
    public ModelMapper defaultMapper() {
        return new ModelMapper();
    }

    @Bean(name = "clientMapper")
    public ModelMapper clientMapper() {
        return new ModelMapper();
        // próxima clase
    }

}
