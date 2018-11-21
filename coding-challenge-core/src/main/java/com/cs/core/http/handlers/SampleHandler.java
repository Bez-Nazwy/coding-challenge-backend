package com.cs.core.http.handlers;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

import com.google.gson.JsonObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class SampleHandler {

    public Mono<ServerResponse> get(ServerRequest request) {
        var body = new JsonObject();
        body.addProperty("message", "Hello world");

        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(body.toString()));
    }
}
