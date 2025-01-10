package com.abernathyclinic.patientdemographics.controller;

import com.abernathyclinic.patientdemographics.model.Patient;
import com.abernathyclinic.patientdemographics.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {
    @Mock
    PatientRepository patientRepository;
    @InjectMocks
    PatientController patientController;

    Patient patient;

    @BeforeEach
    void setUp() {
        patient = Patient.builder()
                .familyName("shin")
                .givenName("david")
                .sex("M")
                .dateOfBirth("03/02/1987")
                .phoneNumber("123-345-6789")
                .homeAddress("123 main st")
                .build();
    }

    @Test
    void add_new_person(){
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        ResponseEntity<Patient> responseEntity = patientController.addPatient("shin","david", "M", "03/02/1987", "123-345-6789", "123 main st");

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }
}