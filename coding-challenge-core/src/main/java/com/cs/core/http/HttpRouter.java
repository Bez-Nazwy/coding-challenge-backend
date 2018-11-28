package com.cs.core.http;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

import com.cs.core.http.handlers.AuthHandler;
import com.cs.core.http.handlers.ItemsHandler;
import com.cs.core.http.handlers.UsersHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HttpRouter {

    @Bean
    public RouterFunction<ServerResponse> routeAuth(AuthHandler handler) {
        return nest(path("/api/auth"),
            RouterFunctions
                .route(POST("/"), handler::authenticate)
        );
    }

    @Bean
    public RouterFunction<ServerResponse> routeUsers(UsersHandler handler) {
        return nest(path("/api/users"),
            RouterFunctions.route(POST("/"), handler::addUser)
        );
    }

    @Bean
    public RouterFunction<ServerResponse> routeItems(ItemsHandler handler) {
        return nest(path("/api/items"),
            RouterFunctions
                .route(GET("/"), handler::getAll)
                .andRoute(GET("/{id}"), handler::getItem)
                .andRoute(POST("/"), handler::addItem)
        );
    }
}