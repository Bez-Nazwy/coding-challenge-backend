package com.cs.core.data.services;

import com.cs.core.data.repositories.UserRepository;
import com.cs.domain.auth.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> getUser(int id) {
        return userRepository
            .findById(id)
            .doOnError(err -> logger.warn("Error occurred when retrieving an user with id {}: {}",
                id, err.getLocalizedMessage()));
    }

    public Mono<User> getUser(String username) {
        return userRepository
            .findByUsername(username)
            .doOnError(err -> logger.warn("Error occurred when retrieving an user with username {}: {}",
                username, err.getLocalizedMessage()));
    }

    public Flux<User> getAll() {
        return userRepository
            .findAll()
            .doOnError(err -> logger.warn("Error occurred when retrieving users: {}",
                err.getLocalizedMessage()));
    }

    public Mono<User> addUser(User user) {
        return userRepository
            .save(user)
            .doOnError(err -> logger.warn("Error occurred when adding an user: {}",
                err.getLocalizedMessage()));
    }
}
