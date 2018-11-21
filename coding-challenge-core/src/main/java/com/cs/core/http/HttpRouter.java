package com.cs.core.http;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

import com.cs.core.http.handlers.UserHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HttpRouter {

    @Bean
    public RouterFunction<ServerResponse> routeUsers(UserHandler handler) {
        return nest(path("/api/users"),
            RouterFunctions
                .route(GET("/"), handler::getAll)
                .andRoute(GET("/{id}"), handler::getUser)
                .andRoute(POST("/"), handler::addUser)
        );
    }
}