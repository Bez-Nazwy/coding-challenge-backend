package com.cs.core.http;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

import com.cs.core.http.handlers.SampleHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HttpRouter {

    @Bean
    public RouterFunction<ServerResponse> routeSample(SampleHandler handler) {
        return nest(path("/api/sample"),
            RouterFunctions.route(GET(""), handler::get)
        );
    }
}