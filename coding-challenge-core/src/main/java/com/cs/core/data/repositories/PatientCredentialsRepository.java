package com.cs.core.data.repositories;

import com.cs.domain.auth.PatientCredentials;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PatientCredentialsRepository extends ReactiveMongoRepository<PatientCredentials, Integer> {

    Mono<PatientCredentials> findPatientCredentialsByPatientNumber(int patientNumber);
}
