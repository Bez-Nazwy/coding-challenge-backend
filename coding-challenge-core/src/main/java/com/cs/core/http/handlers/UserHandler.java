package com.cs.core.http.handlers;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.created;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.cs.core.data.services.UserService;
import com.cs.domain.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    public Mono<ServerResponse> getUser(ServerRequest request) {
        var id = Integer.parseInt(request.pathVariable("id"));
        return userService
            .getUser(id)
            .flatMap(json -> ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(json))
            )
            .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(userService.getAll(), User.class);
    }

    public Mono<ServerResponse> addUser(ServerRequest request) {
        return request
            .bodyToMono(User.class)
            .flatMap(user -> userService.addUser(user))
            .flatMap(user -> created(constructResourceURI(request, user))
                .build()
            )
            .switchIfEmpty(badRequest().build());
    }

    private URI constructResourceURI(ServerRequest request, User user) {
        return request.uri().resolve("/" + user.getId());
    }
}
