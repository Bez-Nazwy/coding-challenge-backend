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
        return userRepository.findById(id);
    }

    public Mono<User> getUser(String username) {
        return userRepository.findByUsername(username);
    }

    public Flux<User> getAll() {
        return userRepository.findAll();
    }

    public Mono<User> addUser(User user) {
        return userRepository
            .existsByUsername(user.getUsername())
            .flatMap(exists -> trySaveUser(user, exists));
    }

    private Mono<User> trySaveUser(User user, boolean exists) {
        if (exists) {
            throw new RuntimeException("User with given name already exists");
        } else {
            return userRepository.save(user);
        }
    }
}
