package com.cs.core.http.handlers;

import com.cs.core.data.services.PatientCredentialsService;
import com.cs.core.data.services.UserService;
import com.cs.core.security.impl.JWTProvider;
import com.cs.domain.auth.AuthWebRequest;
import com.cs.domain.auth.PatientCredentials;
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
    private PatientCredentialsService patientCredentialsService;
    private JWTProvider jwtProvider;
    private BCryptPasswordEncoder encoder;

    @Autowired
    public AuthHandler(UserService userService,
                       PatientCredentialsService patientCredentialsService,
                       JWTProvider jwtProvider,
                       BCryptPasswordEncoder encoder) {
        this.userService = userService;
        this.patientCredentialsService = patientCredentialsService;
        this.jwtProvider = jwtProvider;
        this.encoder = encoder;
    }

    public Mono<ServerResponse> authenticateMobileUser(ServerRequest request) {

        return request
            .bodyToMono(PatientCredentials.class)
            .flatMap(req -> patientCredentialsService
                .getPatientCredentials(req.getPatientNumber())
                .flatMap(creds -> validateCredentialsPassword(req, creds))
            )
            .flatMap(this::constructTokenResponse)
            .switchIfEmpty(constructInvalidUserResponse())
            .onErrorResume(ResponseUtils::handleReactiveError);
    }

    public Mono<ServerResponse> authenticateWebUser(ServerRequest request) {
        return request
            .bodyToMono(AuthWebRequest.class)
            .flatMap(req -> userService
                .getUser(req.getUsername())
                .flatMap(user -> validateUserPassword(req, user))
            )
            .flatMap(this::constructTokenResponse)
            .switchIfEmpty(constructInvalidUserResponse())
            .onErrorResume(ResponseUtils::handleReactiveError);
    }

    private Mono<String> validateUserPassword(AuthWebRequest authRequest, User user) {
        if (encoder.matches(authRequest.getPassword(), user.getPassword())) {
            return Mono.just(jwtProvider.generateToken(user));
        } else {
            return Mono.empty();
        }
    }

    private Mono<String> validateCredentialsPassword(PatientCredentials authRequest, PatientCredentials patientCredentials) {
        if (authRequest.getPassword().equals(patientCredentials.getPassword())) {
            return Mono.just(jwtProvider.generateToken(patientCredentials));
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
            .put("message", "Nieprawidłowa nazwa użytkownika lub hasło")
            .toString();
        return badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(body));
    }
}
