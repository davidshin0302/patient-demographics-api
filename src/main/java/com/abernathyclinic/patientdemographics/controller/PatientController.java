package com.abernathyclinic.patientdemographics.controller;

import com.abernathyclinic.patientdemographics.model.Patient;
import com.abernathyclinic.patientdemographics.model.PatientList;
import com.abernathyclinic.patientdemographics.repository.PatientRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@CrossOrigin
@RequestMapping
public class PatientController {

    @Autowired
    PatientRepository patientRepository;

    @GetMapping("/patients")
    public ResponseEntity<PatientList> getPatients() {
        ResponseEntity<PatientList> responseEntity;

        try {
            PatientList patientList = new PatientList();
            patientList.setPatientList(patientRepository.findAll());

            responseEntity = ResponseEntity.status(HttpStatus.OK)
                    .body(patientList);
            log.info("processed GET request '/patient/data....");
        } catch (RuntimeException ex) {
            log.error("Unable to fetch list of patients");
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    @PutMapping("patient/update/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient updatePatient) {
        ResponseEntity<Patient> responseEntity;

        try {
            Patient patient = patientRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Patient with Id is not found, ID: " + id));

            patient.setGivenName(updatePatient.getGivenName());
            patient.setFamilyName(updatePatient.getFamilyName());
            patient.setDateOfBirth(updatePatient.getDateOfBirth());
            patient.setSex(updatePatient.getSex());
            patient.setPhoneNumber(updatePatient.getPhoneNumber());
            patient.setHomeAddress(updatePatient.getHomeAddress());

            patientRepository.save(patient);
            responseEntity = ResponseEntity.status(HttpStatus.OK).body(patient);

            log.info("processed Put request '/patient/update/....");
        } catch (EntityNotFoundException ex) {
            log.error(ex.getMessage());
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            log.error("Unexpected Error Occurred: {}", ex.getMessage());
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    @PostMapping("patient/add")
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
        } catch (RuntimeException ex) {
            log.error("Unable to add new patent: {}", patient);
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return responseEntity;
    }

/*    Temporarily for API endpoint to use thymeleaf .
    @GetMapping
    public String showPatientPage(Model model) {
        PatientList patientList = new PatientList();

        patientList.setPatientList(patientRepository.findAll());

        model.addAttribute("patientList", patientList.getPatientList());

        return "patient-list";
    }
 */
}
