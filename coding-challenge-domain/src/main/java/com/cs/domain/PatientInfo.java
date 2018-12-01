package com.cs.domain;

public class PatientInfo {

    private int queueNumber;
    private Long serviceTime;
    private String doctor;

    public PatientInfo(int queueNumber, Long serviceTime, String doctor) {
        this.queueNumber = queueNumber;
        this.serviceTime = serviceTime;
        this.doctor = doctor;
    }

    public int getQueueNumber() {
        return queueNumber;
    }

    public Long getServiceTime() {
        return serviceTime;
    }

    public String getDoctor() {
        return doctor;
    }
}
