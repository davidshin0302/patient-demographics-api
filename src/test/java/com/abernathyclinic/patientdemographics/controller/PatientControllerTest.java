package com.abernathyclinic.patientdemographics.controller;

import com.abernathyclinic.patientdemographics.model.Patient;
import com.abernathyclinic.patientdemographics.model.PatientList;
import com.abernathyclinic.patientdemographics.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PatientController.class)
class PatientControllerTest {
    @MockitoBean
    PatientRepository patientRepository;
    @InjectMocks
    PatientController patientController;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    Patient patient;
    PatientList patientList;

    String FILE_PATH = "src/test/java/com/abernathyclinic/patientdemographics/resources/patientList.json";

    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        patientList = new PatientList();

        patientList.setPatientList(new ArrayList<>());
        patientList = objectMapper.readValue(new File(FILE_PATH), PatientList.class);
    }

    @Test
    void add_new_patient() throws Exception {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        mockMvc.perform(post("http://localhost:8081/patient/add?family=TestNone&given=Test&dob=1966-12-31&sex=F&address=1 Brrokside St&phone=100-222-3333")
                ).andExpect(status().isCreated())
                .andExpect(jsonPath("$.givenName").value("Test"))
                .andExpect(jsonPath("$.familyName").value("TestNone"))
                .andExpect(jsonPath("$.dateOfBirth").value("1966-12-31"))
                .andExpect(jsonPath("$.homeAddress").value("1 Brrokside St"))
                .andExpect(jsonPath("$.phoneNumber").value("100-222-3333"));
    }

    @Test
    void add_conflict_patient() throws Exception {
        when(patientRepository.save(any(Patient.class))).thenThrow(new DataIntegrityViolationException("Conflict"));

        mockMvc.perform(post("http://localhost:8081/patient/add?family=TestNone&given=Test&dob=1966-12-31&sex=F&address=1 Brrokside St&phone=100-222-3333")
        ).andExpect(status().isConflict());
    }

    @Test
    void add_bad_request_patient() throws Exception {
        when(patientRepository.save(any(Patient.class))).thenThrow(new RuntimeException("Bad Request"));

        mockMvc.perform(post("http://localhost:8081/patient/add?family=&given=&dob=&sex=&address=&phone="))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_patients_data() throws Exception {
        when(patientRepository.findAll()).thenReturn(patientList.getPatientList());

        mockMvc.perform(get("http://localhost:8081/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientList", hasSize(4)));
    }

    @Test
    void get_bad_request_patients_data() throws Exception {
        when(patientRepository.findAll()).thenThrow(new RuntimeException("Bad Request"));

        mockMvc.perform(get("http://localhost:8081/patients"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void update_patient() throws Exception {
        Optional<Patient> optionalPatient = Optional.ofNullable(patientList.getPatientList().isEmpty() ? null : patientList.getPatientList().getFirst());
        when(patientRepository.findById(any(Long.class))).thenReturn(optionalPatient);

        Patient updatePatient = optionalPatient.get();

        updatePatient.setGivenName("teeeeet");
        mockMvc.perform(put("http://localhost:8081/patient/update/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePatient)))
                .andExpectAll(status().isOk(), jsonPath("$.givenName").value("teeeeet"));
    }

    @Test
    void update_patient_not_found() throws Exception {
        Patient updatePatient = Patient.builder().
                givenName("")
                .familyName("")
                .sex("M")
                .dateOfBirth("123").build();

        when(patientRepository.findById(any(Long.class))).thenThrow(EntityNotFoundException.class);

        mockMvc.perform(put("http://localhost:8081/patient/update/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePatient)))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_patient_internal_error() throws Exception {
        Patient updatePatient = Patient.builder().
                givenName("")
                .familyName("")
                .sex("M")
                .dateOfBirth("123").build();

        when(patientRepository.findById(any(Long.class))).thenThrow(RuntimeException.class);

        mockMvc.perform(put("http://localhost:8081/patient/update/22222")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePatient)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void delete_patient() throws Exception {
        Optional<Patient> optionalPatient = Optional.ofNullable(patientList.getPatientList().isEmpty() ? null : patientList.getPatientList().getFirst());
        when(patientRepository.findById(any(Long.class))).thenReturn(optionalPatient);

        Patient deletePatient = optionalPatient.get();

        mockMvc.perform(delete("http://localhost:8081/patient/delete/11")
                .content(objectMapper.writeValueAsString(deletePatient)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_patient_not_found() throws Exception {
        Patient deletePatient = Patient.builder().
                givenName("No Exist")
                .familyName("")
                .sex("M")
                .dateOfBirth("0000").build();

        when(patientRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        mockMvc.perform(delete("http://localhost:8081/patient/delete/1111")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deletePatient)))
                .andExpect(status().isNotFound());
    }

    /*    Temporarily for API endpoint to use thymeleaf .
    @Test
    void show_patient_page() {
        when(patientRepository.findAll()).thenReturn(patientList.getPatientList());

        String viewPage = patientController.showPatientPage(model);

        assertEquals("patient-list", viewPage);
        verify(model).addAttribute("patientList", patientList.getPatientList());
        verify(patientRepository).findAll();
    }
     */
}