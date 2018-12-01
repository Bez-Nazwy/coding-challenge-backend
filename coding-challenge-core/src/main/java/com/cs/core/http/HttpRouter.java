package com.cs.core.http;

import com.cs.core.http.handlers.AuthHandler;
import com.cs.core.http.handlers.PatientHandler;
import com.cs.core.http.handlers.UsersHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;

@Configuration
public class HttpRouter {

    @Bean
    public RouterFunction<ServerResponse> routeAuth(AuthHandler handler) {
        return nest(path("/api/auth"),
            RouterFunctions
                .route(POST("/web"), handler::authenticateWebUser)
                .andRoute(POST("/mobile"), handler::authenticateMobileUser)
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
                .route(GET("/{doctor:[a-z]+}"), handler::getPatientList)
                .andRoute(GET("/{patientNumber:[0-9]+}"), handler::getPatientInfo)
                .andRoute(POST("/"), handler::addPatient)
                .andRoute(GET("/"), handler::getAllPatientLists)
        );
    }
}