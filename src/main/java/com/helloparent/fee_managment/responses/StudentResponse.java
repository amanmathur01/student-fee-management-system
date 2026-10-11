
package com.helloparent.fee_managment.responses;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.helloparent.fee_managment.enums.Gender;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

	private Long id;

	// Personal details
	private String firstName;
	private String lastName;
	private LocalDate dateOfBirth;
	private Gender gender;

	// Contact details
	private String email;
	private String phone;

	// Parent details
	private String fatherName;
	private String motherName;

	// Address details
	private String address;
	private String city;
	private String state;
	private String postalCode;

	// Academic details
	private String className;
	private String section;

	// Fee details
	private BigDecimal totalFee;

	// System details
	private LocalDateTime createdAt;
	private boolean active;
}
