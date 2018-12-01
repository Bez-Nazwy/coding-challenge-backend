package com.cs.core.data.repositories;

import com.cs.domain.Patient;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PatientRepository extends ReactiveMongoRepository<Patient, String> {

    Mono<Patient> findByPatientNumber(int patientNumber);
    Mono<Patient> deletePatientById(String id);

}
