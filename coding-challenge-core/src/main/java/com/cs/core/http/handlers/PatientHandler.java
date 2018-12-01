package com.cs.core.http.handlers;

import com.cs.core.data.services.PatientService;
import com.cs.domain.Doctor;
import com.cs.domain.Patient;
import com.cs.utils.ResponseUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

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

    public Mono<ServerResponse> getAllPatientLists(ServerRequest request) {
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(patientService.getAllPatientLists().map(JSONObject::toString), String.class);
    }

    public Mono<ServerResponse> addPatient(ServerRequest request) {
        return request
            .bodyToMono(Patient.class)
            .flatMap(patientService::addPatient)
            .flatMap(creds -> ok().body(fromObject(creds)))
            .switchIfEmpty(badRequest().build())
            .onErrorResume(ResponseUtils::handleReactiveError);
    }

    public Mono<ServerResponse> getPatientInfo(ServerRequest request) {
        var patientNumber = Integer.parseInt(request.pathVariable("patientNumber"));
        var body = new JSONObject();
        var patient = new AtomicReference<Patient>();

        return patientService
            .getPatient(patientNumber)
            .doOnNext(patient::set)
            .doOnNext(p -> body.put("queueNumber", p.getPriority()))
            .map(Patient::getDoctor)
            .doOnNext(doctor -> body.put("doctor", doctor.name()))
            .flatMapMany(doctor -> patientService.getPatientList(doctor))
            .filter(p -> p.getPriority() < patient.get().getPriority())
            .map(Patient::getServiceTime)
            .switchIfEmpty(Mono.just(0))
            .reduce((time1, time2) -> time1 + time2)
            .map(serviceTime -> body.put("serviceTime", System.currentTimeMillis() + minutesToMillis(serviceTime)).toString())
            .flatMap(b -> ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(b))
            )
            .switchIfEmpty(badRequest().build());
    }

    private Long minutesToMillis(int minutes) {
        return minutes * 60L * 1000L;
    }
}
