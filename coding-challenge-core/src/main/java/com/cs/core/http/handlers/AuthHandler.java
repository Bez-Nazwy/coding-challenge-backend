package com.cs.core.http.handlers;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.cs.core.data.services.UserService;
import com.cs.core.security.impl.JWTProvider;
import com.cs.domain.auth.AuthRequest;
import com.cs.domain.auth.User;
import com.cs.utils.ResponseUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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
            .switchIfEmpty(invalidUserResponse())
            .onErrorResume(ResponseUtils::handleReactiveError);
    }

    private Mono<ServerResponse> constructTokenResponse(String token) {
        var body = new JSONObject()
            .put("message", "Authenticated")
            .put("token", token)
            .toString();
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(body));
    }

    private Mono<ServerResponse> invalidUserResponse() {
        var body = new JSONObject()
            .put("message", "Invalid username and password combination")
            .toString();
        return badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(body));
    }

    private Mono<String> validatePassword(AuthRequest authRequest, User user) {
        if (user.getPassword().equals(authRequest.getPassword())) {
            return Mono.just(jwtProvider.generateToken(user));
        } else {
            return Mono.empty();
        }
    }
}
