package com.cs.core.data.services;

import com.cs.core.data.repositories.UserRepository;
import com.cs.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> getUser(int id) {
        return userRepository.findById(id);
    }

    public Flux<User> getAll() {
        return userRepository.findAll();
    }

    public Mono<User> addUser(User user) {
        return userRepository.save(user);
    }
}
