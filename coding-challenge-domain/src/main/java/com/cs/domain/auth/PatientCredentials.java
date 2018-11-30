package com.cs.domain.auth;

import com.cs.domain.PasswordGenerator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "patientCredentials")
public class PatientCredentials { //todo co z interfejsem UserDetails


    @Id
    private String id;
    private int patientNumber;
    private String password;

    public PatientCredentials() {
    }

    public PatientCredentials(int patientNumber) {
        this.patientNumber = patientNumber;
        this.password = PasswordGenerator.generatePassword(6,
            PasswordGenerator.ALPHA +
                PasswordGenerator.NUMERIC +
                PasswordGenerator.ALPHA_CAPS);
    }

    public int getPatientNumber() {
        return patientNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String newPassword) {
        this.password = newPassword;
    }
}
