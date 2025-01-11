package com.abernathyclinic.patientdemographics.controller;

import com.abernathyclinic.patientdemographics.model.Patient;
import com.abernathyclinic.patientdemographics.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {
    @Mock
    PatientRepository patientRepository;
    @InjectMocks
    PatientController patientController;

    Patient patient;

    @Test
    void add_new_patient() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        ResponseEntity<Patient> responseEntity = patientController.addPatient("shin", "david", "M", "03/02/1987", "123-345-6789", "123 main st");

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getBody().getFamilyName(), "shin");
        assertEquals(responseEntity.getBody().getGivenName(), "david");
        assertEquals(responseEntity.getBody().getDateOfBirth(), "M");
        assertEquals(responseEntity.getBody().getSex(), "03/02/1987");
    }

    @Test
    void add_conflict_patient() {
        when(patientRepository.save(any(Patient.class))).thenThrow(new DataIntegrityViolationException("Conflict"));

        ResponseEntity<Patient> responseEntity = patientController.addPatient("shin", "david", "M", "03/02/1987", "123-345-6789", "123 main st");

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void add_bad_request_patient() {
        when(patientRepository.save(any(Patient.class))).thenThrow(new RuntimeException("Bad Request"));

        ResponseEntity<Patient> responseEntity = patientController.addPatient(" ", " ", " ", " ", " ", " ");

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }
}