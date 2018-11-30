package com.cs.core.data.services;

import com.cs.core.data.repositories.UserRepository;
import com.cs.domain.auth.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
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

    private void encodeUserPassword(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
    }


    private Mono<User> trySaveUser(User user, boolean exists) {
        if (exists) {
            throw new RuntimeException("User with given name already exists");
        } else {
            encodeUserPassword(user);
            return userRepository.save(user);
        }
    }
}
