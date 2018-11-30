package com.cs.core.http;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

import com.cs.core.http.handlers.AuthHandler;
import com.cs.core.http.handlers.PatientHandler;
import com.cs.core.http.handlers.UsersHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class HttpRouter {

    @Bean
    public RouterFunction<ServerResponse> routeWebAuth(AuthHandler handler) {
        return nest(path("/api/web/auth"),
            RouterFunctions
                .route(POST("/"), handler::authenticateWebUser)
        );
    }

    @Bean
    public RouterFunction<ServerResponse> routeMobileAuth(AuthHandler handler) {
        return nest(path("/api/mobile/auth"),
                RouterFunctions
                        .route(POST("/"), handler::authenticateMobileUser)
        );
    }

    @Bean
    public RouterFunction<ServerResponse> routeUsers(UsersHandler handler) {
        return nest(path("/api/users"),
            RouterFunctions.route(POST("/"), handler::addUser)
        );
    }

    @Bean
    public RouterFunction<ServerResponse> routePatients(PatientHandler handler) {
        return nest(path("/api/patients"),
            RouterFunctions
                .route(GET("/{doctor}"), handler::getPatientList)
                .andRoute(POST("/"), handler::addPatient)
        );
    }
}