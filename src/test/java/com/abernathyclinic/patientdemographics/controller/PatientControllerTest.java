package com.abernathyclinic.patientdemographics.controller;

import com.abernathyclinic.patientdemographics.model.Patient;
import com.abernathyclinic.patientdemographics.repository.PatientRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {
    @Mock
    PatientRepository patientRepository;
    @Mock
    Model model;
    @InjectMocks
    PatientController patientController;
    @Autowired
    ObjectMapper objectMapper;

    Patient patient;
    List<Patient> patientList;

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        String filePath = "src/test/java/com/abernathyclinic/patientdemographics/resources/patientList.json";
        patientList = objectMapper.readValue(new File(filePath), new TypeReference<List<Patient>>() {
        });
    }

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

    @Test
    void get_patients_data() {
        when(patientRepository.findAll()).thenReturn(patientList);

        ResponseEntity<List<Patient>> responseEntity = patientController.getPatients();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(patientRepository.findAll().size(), patientList.size());
    }

    @Test
    void get_bad_request_patients_data() {
        when(patientRepository.findAll()).thenThrow(new RuntimeException("Bad Request"));

        ResponseEntity<List<Patient>> responseEntity = patientController.getPatients();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void show_patient_page() {
        when(patientRepository.findAll()).thenReturn(patientList);

        String viewPage = patientController.showPatientPage(model);

        assertEquals("patient-list", viewPage);
        verify(model).addAttribute("patientList", patientList);
        verify(patientRepository).findAll();
    }
}