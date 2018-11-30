package com.cs.core.http.handlers;

import com.cs.core.data.services.PatientService;
import com.cs.domain.Doctor;
import com.cs.domain.Patient;
import com.cs.utils.ResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class PatientHandler {

    private PatientService patientService;

    @Autowired
    public PatientHandler(PatientService patientService) {
        this.patientService = patientService;
    }


    public Mono<ServerResponse> getPatient(ServerRequest request) {
        var id = request.pathVariable("id");
        return patientService
            .getPatient(id)
            .flatMap(patient -> ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(patient))
            )
            .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> getPatientList(ServerRequest request) {
        var doctor = Doctor.valueOf(request.pathVariable("doctor").toUpperCase());
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(patientService.getPatientList(doctor), Patient.class);
    }

    public Mono<ServerResponse> addPatient(ServerRequest request) {
        return request
            .bodyToMono(Patient.class)
            .flatMap(patientService::addPatient)
            .flatMap(patientCreds -> ok().body(fromObject(patientCreds)))
            .switchIfEmpty(badRequest().build())
            .onErrorResume(ResponseUtils::handleReactiveError);
    }
}
