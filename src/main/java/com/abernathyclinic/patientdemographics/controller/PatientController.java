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

import java.util.Optional;

/**
 * REST controller for managing Patient resources.
 */
@Slf4j
@Controller
@CrossOrigin
@RequestMapping
public class PatientController {

    @Autowired
    PatientRepository patientRepository;

    /**
     * Retrieves a list of all patients.
     *
     * @return ResponseEntity containing a PatientList object with all patients, or an error response.
     */
    @GetMapping("/patients")
    public ResponseEntity<PatientList> getPatients() {
        ResponseEntity<PatientList> responseEntity;

        try {
            PatientList patientList = new PatientList();
            patientList.setPatientList(patientRepository.findAll());

            responseEntity = ResponseEntity.status(HttpStatus.OK).body(patientList);
            log.info("processed GET request '/patient/data....");
        } catch (RuntimeException ex) {
            log.error("Unable to fetch list of patients");
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    /**
     * Retrieves a patient by ID.
     *
     * @param id The ID of the patient to retrieve.
     * @return ResponseEntity containing the Patient object, or an error response.
     */
    @GetMapping("patient/get/{id}")
    public ResponseEntity<Patient> getPatients(@PathVariable Long id) {
        ResponseEntity<Patient> responseEntity;

        try {
            Patient patient = new Patient();
            patient = patientRepository.findById(id).orElse(null);

            responseEntity = ResponseEntity.status(HttpStatus.OK).body(patient);
            log.info("processed GET request '/patient/id/{}....", id);
        } catch (RuntimeException ex) {
            log.error("Unable to fetch patients");
            log.error(ex.getMessage());

            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    /**
     * Updates an existing patient's information.
     *
     * @param id            The ID of the patient to update.
     * @param updatePatient The Patient object containing the updated information.
     * @return ResponseEntity containing the updated Patient object, or an error response.
     */
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
        } catch (RuntimeException ex) {
            log.error("Unexpected Error Occurred: {}", ex.getMessage());
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return responseEntity;
    }

    /**
     * Adds a new patient.
     *
     * @param familyName  The patient's family name.
     * @param givenName   The patient's given name.
     * @param dateOfBirth The patient's date of birth.
     * @param sex         The patient's sex.
     * @param homeAddress The patient's home address.
     * @param phoneNumber The patient's phone number.
     * @return ResponseEntity containing the created Patient object, or an error response.
     */
    @PostMapping("patient/add")
    public ResponseEntity<Patient> addPatient(@Valid @RequestParam("family") String familyName, @Valid @RequestParam("given") String givenName, @Valid @RequestParam("dob") String dateOfBirth, @Valid @RequestParam("sex") String sex, @RequestParam("address") String homeAddress, @RequestParam("phone") String phoneNumber) {

        ResponseEntity<Patient> responseEntity;

        Patient patient = Patient.builder().familyName(familyName).givenName(givenName).dateOfBirth(dateOfBirth).sex(sex).homeAddress(homeAddress).phoneNumber(phoneNumber).build();

        try {
            patientRepository.save(patient);

            responseEntity = ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(patient);

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

    /**
     * Deletes a patient by ID.
     *
     * @param id The ID of the patient to delete.
     * @return ResponseEntity with HttpStatus.OK if successful, or HttpStatus.NOT_FOUND if the patient is not found.
     */
    @DeleteMapping("/patient/delete/{id}")
    public ResponseEntity<HttpStatus> deletePatient(@PathVariable Long id) {
        ResponseEntity<HttpStatus> responseEntity;
        Optional<Patient> patientOptional = patientRepository.findById(id);

        if (patientOptional.isPresent()) {
            patientRepository.delete(patientOptional.get());

            responseEntity = ResponseEntity.status(HttpStatus.OK).build();
            log.info("processed DELETE request '/patient'...");
        } else {
            log.error("Couldn't find the matching Patient, Id: {}", id);
            responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return responseEntity;
    }
}
