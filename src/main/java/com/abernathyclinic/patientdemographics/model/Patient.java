package com.abernathyclinic.patientdemographics.model;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "given_name", nullable = false)
    private String givenName;

    @Column(name = "family_name", nullable = false)
    private String familyName;

    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;

    @Column(nullable = false)
    private String sex;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "phone_number")
    private String phoneNumber;
}
