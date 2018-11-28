package com.cs.utils;


import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;

import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

public class ResponseUtils {

    public static Mono<ServerResponse> handleReactiveError(Throwable error) {
        var body = new JSONObject()
            .put("message", error.getLocalizedMessage())
            .toString();
        return badRequest()
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromObject(body));
    }
}
