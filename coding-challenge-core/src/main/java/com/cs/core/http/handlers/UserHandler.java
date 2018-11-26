package com.cs.core.http.handlers;

import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.created;

import com.cs.core.data.services.UserService;
import com.cs.domain.auth.User;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class UserHandler {

    private UserService userService;

    @Autowired
    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public Mono<ServerResponse> addUser(ServerRequest request) {
        return request
            .bodyToMono(User.class)
            .flatMap(userService::addUser)
            .flatMap(user -> created(constructResourceURI(request, user)).build())
            .switchIfEmpty(badRequest().build());
    }

    private URI constructResourceURI(ServerRequest request, User user) {
        return request.uri().resolve("/" + user.getId());
    }
}
