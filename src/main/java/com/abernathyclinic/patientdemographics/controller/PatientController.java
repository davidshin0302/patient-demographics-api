package com.abernathyclinic.patientdemographics.controller;

import com.abernathyclinic.patientdemographics.model.Patient;
import com.abernathyclinic.patientdemographics.repository.PatientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    PatientRepository patientRepository;

    @PostMapping("/add")
    public ResponseEntity<Patient> addPatient(
            @RequestParam("family") String familyName,
            @RequestParam("given") String givenName,
            @RequestParam("dob") String dateOfBirth,
            @RequestParam("sex") String sex,
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
        } catch (NullPointerException ex) {
            log.error("Can't not save Null values {}", patient);
            log.error(ex.getMessage());

            responseEntity = new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return responseEntity;
    }
}
