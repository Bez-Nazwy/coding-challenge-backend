package com.cs.core.http.handlers;

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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private BCryptPasswordEncoder encoder;

    @Autowired
    public AuthHandler(UserService userService, JWTProvider jwtProvider, BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.encoder = encoder;
    }

    public Mono<ServerResponse> authenticate(ServerRequest request) {
        return request
            .bodyToMono(AuthRequest.class)
            .flatMap(req -> userService
                .getUser(req.getUsername())
                .flatMap(user -> validatePassword(req, user))
            )
            .flatMap(this::constructTokenResponse)
            .switchIfEmpty(constructInvalidUserResponse())
            .onErrorResume(ResponseUtils::handleReactiveError);
    }

    private Mono<String> validatePassword(AuthRequest authRequest, User user) {
        if (encoder.matches(authRequest.getPassword(), user.getPassword())) {
            return Mono.just(jwtProvider.generateToken(user));
        } else {
            return Mono.empty();
        }
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

    private Mono<ServerResponse> constructInvalidUserResponse() {
        var body = new JSONObject()
            .put("message", "Invalid username and password combination")
            .toString();
        return badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(body));
    }
}
