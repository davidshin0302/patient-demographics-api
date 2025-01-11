package com.abernathyclinic.patientdemographics.repository;

import com.abernathyclinic.patientdemographics.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
}
