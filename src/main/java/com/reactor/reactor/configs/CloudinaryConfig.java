package com.reactor.reactor.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "escalab-academy",
                "api_key", "962167876617839",
                "api_secret", "9jaYaLHrYXWVpRM-Nt1hwWLs1os"));
    }

}
