package com.cs.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.cs")
@EnableReactiveMongoRepositories(basePackages = "com.cs")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
