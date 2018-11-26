package com.cs.core.http.handlers;

import com.cs.core.data.services.UserService;
import com.cs.core.security.impl.JWTProvider;
import com.cs.domain.auth.AuthRequest;
import com.cs.domain.auth.User;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class AuthHandler {

    private static final Logger logger = LoggerFactory.getLogger(AuthHandler.class);

    private UserService userService;
    private JWTProvider jwtProvider;

    @Autowired
    public AuthHandler(UserService userService, JWTProvider jwtProvider) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
    }

    public Mono<ServerResponse> authenticate(ServerRequest request) {
        return request
            .bodyToMono(AuthRequest.class)
            .flatMap(req -> userService
                .getUser(req.getUsername())
                .flatMap(user -> validatePassword(req, user))
            )
            .flatMap(this::constructTokenResponse)
            .doOnError(err -> logger.warn("Error occurred when authenticating request", err))
            .switchIfEmpty(constructBadRequestResponse());
    }

    private Mono<ServerResponse> constructTokenResponse(String token) {
        var result = new JsonObject();
        result.addProperty("message", "Authenticated");
        result.addProperty("token", token);
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(result.toString()));
    }

    private Mono<ServerResponse> constructBadRequestResponse() {
        var result = new JsonObject();
        result.addProperty("message", "Invalid username and password combination");
        return badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(result.toString()));
    }

    private Mono<String> validatePassword(AuthRequest authRequest, User user) {
        if (user.getPassword().equals(authRequest.getPassword())) {
            return Mono.just(jwtProvider.generateToken(user));
        } else {
            return Mono.empty();
        }
    }
}
