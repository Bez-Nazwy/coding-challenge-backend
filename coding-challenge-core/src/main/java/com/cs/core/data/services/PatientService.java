package com.cs.core.data.services;

import com.cs.core.data.repositories.PatientRepository;
import com.cs.domain.Doctor;
import com.cs.domain.Patient;
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
    private PatientRepository patientRepository;

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public Mono<Patient> getPatient(String id) {
        return patientRepository.findById(id);
    }

    public Flux<Patient> getPatientList(Doctor doctor) {
        return patientRepository
            .findAll()
            .filter(patient -> patient.getDoctor().equals(doctor))
            .sort(Comparator.comparing(Patient::getPriority));
    }

    public Mono<Patient> addPatient(Patient patient) {
        return patientRepository.save(patient);
    }
}
