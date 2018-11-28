package com.cs.core.data.repositories;

import com.cs.domain.auth.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, Integer> {

    Mono<User> findByUsername(String username);
    Mono<Boolean> existsByUsername(String username);
}
