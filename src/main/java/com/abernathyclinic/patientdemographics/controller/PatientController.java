package com.abernathyclinic.patientdemographics.controller;

import com.abernathyclinic.patientdemographics.model.Patient;
import com.abernathyclinic.patientdemographics.repository.PatientRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientRepository patientRepository;

    @PostMapping("/add")
    public ResponseEntity<Patient> addPatient(
            @Valid @RequestParam("family") String familyName,
            @Valid @RequestParam("given") String givenName,
            @Valid @RequestParam("dob") String dateOfBirth,
            @Valid @RequestParam("sex") String sex,
            @RequestParam("address") String homeAddress,
            @RequestParam("phone") String phoneNumber) {

        ResponseEntity<Patient> responseEntity;

        Patient patient = Patient.builder()
                .familyName(familyName)
                .givenName(givenName)
                .dateOfBirth(dateOfBirth)
                .sex(sex)
                .homeAddress(homeAddress)
                .phoneNumber(phoneNumber)
                .build();

        try {
            patientRepository.save(patient);

            responseEntity = ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(patient);

            log.info("processed POST request '/patient'...");
        } catch (DataIntegrityViolationException ex) {
            log.error("The Patient already exist in the system: {}", patient);
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception ex) {
            log.error("Unable to add new patent: {}", patient);
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return responseEntity;
    }
}
