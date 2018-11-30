package com.cs.core.data.services;

import com.cs.core.data.repositories.PatientCredentialsRepository;
import com.cs.domain.auth.PatientCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PatientCredentialsService {

    private PatientCredentialsRepository patientCredentialsRepository;

    @Autowired
    public PatientCredentialsService(PatientCredentialsRepository patientCredentialsRepository) {
        this.patientCredentialsRepository = patientCredentialsRepository;
    }

    public Mono<PatientCredentials> getPatientCredentials(int patientNumber) {

        var patientCredentials = new PatientCredentials(patientNumber);

        return patientCredentialsRepository
                .save(patientCredentials)
                .flatMap(pc -> patientCredentialsRepository
                        .findPatientCredentialsByNumber(patientNumber)
                );
        //return patientCredentialsRepository.findPatientCredentialsByNumber(patientNumber);
    }

    public Mono<PatientCredentials> addPatientCredentials(int patientNumber) {
        PatientCredentials patientCredentials = new PatientCredentials(patientNumber);
        return patientCredentialsRepository.save(patientCredentials);
    }

    //todo deletePatient
}
