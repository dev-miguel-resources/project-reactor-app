package com.reactor.reactor.configs;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MongoConfig implements InitializingBean {

    @Lazy
    private final MappingMongoConverter converter;

    @Override
    public void afterPropertiesSet() throws Exception {
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    }

}
