package com.cs.core.http.handlers;

import com.cs.core.data.services.PatientService;
import com.cs.core.data.services.UserService;
import com.cs.domain.Doctor;
import com.cs.domain.Patient;
import com.cs.utils.ResponseUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@Component
public class PatientHandler {

    private static final Logger logger = LoggerFactory.getLogger(PatientHandler.class);
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

    public Mono<ServerResponse> postponePatient(ServerRequest request) {
        var patientNumber = Integer.parseInt(request.pathVariable("patientNumber"));
        var patient = new AtomicReference<Patient>();

        return patientService
            .getPatient(patientNumber)
            .doOnNext(patient::set)
            .flatMapMany(p -> patientService.getPatientList(patient.get().getDoctor()))
            .filter(p -> p.getPriority() == patient.get().getPriority() + 1)
            .next()
            .doOnNext(p -> p.setPriority(p.getPriority() - 1))
            .flatMap(patientService::updatePatient)
            .map(p -> {
                var pat = patient.get();
                pat.setPriority(pat.getPriority() + 1);
                return pat;
            })
            .flatMap(patientService::updatePatient)
            .flatMap(b -> ok().build())
            .switchIfEmpty(badRequest().build());
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
            .reduce((time1, time2) -> time1 + time2)
            .switchIfEmpty(Mono.just(0))
            .map(serviceTime -> body.put(
                "serviceTime",
                patient.get().getRegistrationTimestamp() + minutesToMillis(serviceTime)).toString()
            )
            .flatMap(b -> ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromObject(b))
            )
            .onErrorResume(err -> notFound().build());
    }

    public Mono<ServerResponse> deletePatient(ServerRequest request) {
        var patientNumber = Integer.parseInt(request.pathVariable("patientNumber"));
        return patientService
            .deletePatient(patientNumber)
                .doOnError(err -> logger.info("dupa erro " + err.getLocalizedMessage()))
            .flatMap(it -> ok().build())
            .onErrorResume(err -> notFound().build());
    }

    private Long minutesToMillis(int minutes) {
        return minutes * 60L * 1000L;
    }
}
