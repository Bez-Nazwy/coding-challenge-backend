package com.cs.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@SpringBootApplication
@ComponentScan(basePackages = "com.cs")
@EnableReactiveMongoRepositories(basePackages = "com.cs")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }


}
