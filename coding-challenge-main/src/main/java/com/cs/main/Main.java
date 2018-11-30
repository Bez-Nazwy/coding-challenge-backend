package com.cs.main;

import com.cs.core.data.services.UserService;
import com.cs.domain.auth.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@SpringBootApplication
@ComponentScan(basePackages = "com.cs")
@EnableReactiveMongoRepositories(basePackages = "com.cs")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public Disposable createSampleNurseAccount(UserService userService, BCryptPasswordEncoder encoder) {
        var user = new User("nurse", encoder.encode("nurse123"));

        return userService
            .addUser(user)
            .onErrorResume(err -> Mono.empty())
            .subscribe();
    }
}
