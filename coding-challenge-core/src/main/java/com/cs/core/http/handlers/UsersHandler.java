package com.cs.core.http.handlers;

import com.cs.core.data.services.UserService;
import com.cs.domain.auth.User;
import com.cs.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.created;

@Component
public class UsersHandler {

    private UserService userService;
    private BCryptPasswordEncoder encoder;

    @Autowired
    public UsersHandler(UserService userService, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.encoder = encoder;
    }

    public Mono<ServerResponse> addUser(ServerRequest request) {
        return request
            .bodyToMono(User.class)
            .flatMap(userService::addUser)
            .flatMap(user -> created(constructResourceURI(request, user)).build())
            .switchIfEmpty(badRequest().build())
            .onErrorResume(ResponseUtils::handleReactiveError);
    }

    private URI constructResourceURI(ServerRequest request, User user) {
        return request.uri().resolve("/" + user.getId());
    }
}
