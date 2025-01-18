package com.abernathyclinic.patientdemographics.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patients", uniqueConstraints = {
        @UniqueConstraint(columnNames = "date_of_birth"), // dob must be unique
        @UniqueConstraint(columnNames = {"given_name", "family_name"}), // combination of given and family name must be unique.
})
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "given_name", nullable = false)
    private String givenName;

    @NotBlank
    @Column(name = "family_name", nullable = false)
    private String familyName;

    @NotBlank
    @Column(name = "date_of_birth", nullable = false)
    private String dateOfBirth;

    @NotBlank
    @Column(nullable = false)
    private String sex;

    @Column(name = "home_address")
    private String homeAddress;

    @Column(name = "phone_number")
    private String phoneNumber;
}
