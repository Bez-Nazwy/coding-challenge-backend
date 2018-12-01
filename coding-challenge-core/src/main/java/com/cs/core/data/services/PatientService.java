package com.cs.core.data.services;

import com.cs.core.data.repositories.PatientRepository;
import com.cs.domain.Doctor;
import com.cs.domain.Patient;
import com.cs.domain.auth.PatientCredentials;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Service
public class PatientService {

    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    private static int nextNumber = 0;
    private PatientRepository patientRepository;
    private PatientCredentialsService patientCredentialsService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          PatientCredentialsService patientCredentialsService) {
        this.patientRepository = patientRepository;
        this.patientCredentialsService = patientCredentialsService;
    }

    public Mono<Patient> getPatient(String id) {
        return patientRepository.findById(id);
    }

    public Mono<Patient> getPatient(int patientNumber) {
        return patientRepository.findByPatientNumber(patientNumber);
    }

    public Flux<Patient> getPatientList(Doctor doctor) {
        return patientRepository
            .findAll()
            .filter(patient -> patient.getDoctor().equals(doctor))
            .sort(Comparator.comparing(Patient::getPriority));
    }

    public Mono<JSONObject> getAllPatientLists() {
        var json = new JSONObject();
        return Flux
            .fromArray(Doctor.values())
            .flatMap(doctor -> getPatientList(doctor)
                .collectList()
                .map(list -> json.put(doctor.name(), new JSONArray(list)))
            )
            .takeLast(1)
            .next();
    }

    public Mono<PatientCredentials> addPatient(Patient patient) {
        var number = nextNumber++;
        patient.setRegistrationTimestamp(System.currentTimeMillis());
        patient.setPatientNumber(number);

        return getPatientList(patient.getDoctor())
            .filter(p -> p.getPriority() >= patient.getPriority())
            .doOnNext(p -> p.setPriority(p.getPriority() + 1))
            .flatMap(patientRepository::save)
            .then(patientRepository
                .save(patient)
                .flatMap(p -> patientCredentialsService.addPatientCredentials(number)));
    }

    public Mono<Void> deletePatient(String id){
        return patientRepository
                .existsById(id)
                .flatMap(exists -> tryDeletePatient(id, exists));
    }

    private Mono<Void> tryDeletePatient(String id, boolean exists) {
        if (exists) {
            return patientRepository.deleteById(id);
        } else {
            throw new RuntimeException("Patient with given id doesn't exists");
        }
    }
}
