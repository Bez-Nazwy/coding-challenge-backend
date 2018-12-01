package com.cs.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.print.Doc;

@Document(collection = "patients")
public class Patient {

    @Id
    private String id;
    private String name;
    private String surname;
    private String diagnose;
    private int serviceTime;
    private String peselNumber;
    private Doctor doctor;
    private int priority;
    private int patientNumber;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getDiagnose() {
        return diagnose;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public String getPeselNumber() {
        return peselNumber;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public int getPriority() {
        return priority;
    }

    public int getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(int patientNumber) {
        this.patientNumber = patientNumber;
    }
}
