package com.helloparent.fee_managment.requests;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.helloparent.fee_managment.enums.Gender;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRequest {

	// Student personal details
	@NotBlank(message = "First name is required")
	@Size(max = 50, message = "First name cannot exceed 50 characters")
	@Pattern(regexp = "^[A-Za-z]+(?:[ .'-][A-Za-z]+)*$", message = "Name can contain only letters, spaces, hyphens, apostrophes and periods")
	private String firstName;

	@NotBlank(message = "Last name is required")
	@Size(max = 50, message = "Last name cannot exceed 50 characters")
	@Pattern(regexp = "^[A-Za-z]+(?:[ .'-][A-Za-z]+)*$", message = "Name can contain only letters, spaces, hyphens, apostrophes and periods")
	private String lastName;

	@NotNull(message = "Date of birth is required")
	@Past(message = "Date of birth must be in the past")
	private LocalDate dateOfBirth;

	@NotNull(message = "Gender is required")
	private Gender gender;

	// Contact details
	@NotBlank(message = "Email is required")
	@Email(message = "Please enter a valid email")
	@Size(max = 150, message = "Email cannot exceed 150 characters")
	private String email;

	@Pattern(regexp = "^[0-9]{10}$", message = "Phone number must contain exactly 10 digits")
	private String phone;

	// Parent/guardian details
	@NotBlank(message = "Father's name is required")
	@Pattern(regexp = "^[A-Za-z]+(?:[ .'-][A-Za-z]+)*$", message = "Name can contain only letters, spaces, hyphens, apostrophes and periods")
	private String fatherName;

	private String motherName;

	// Address details
	@NotBlank(message = "Address is required")
	@Size(max = 250, message = "Address cannot exceed 250 characters")
	private String address;

	@NotBlank(message = "City is required")
	@Size(max = 100, message = "City cannot exceed 100 characters")
	private String city;

	@NotBlank(message = "State is required")
	@Size(max = 100, message = "State cannot exceed 100 characters")
	private String state;

	@NotBlank(message = "Postal code is required")
	@Pattern(regexp = "^[0-9]{6}$", message = "Postal code must contain exactly 6 digits")
	private String postalCode;

	// Academic details
	@NotBlank(message = "Class is required")
	@Size(max = 50, message = "Class cannot exceed 50 characters")
	private String className;

	@Size(max = 20, message = "Section cannot exceed 20 characters")
	private String section;

	// Fee details
	@NotNull(message = "Total fee is required")
	@DecimalMin(value = "0.0", message = "Total fee cannot be negative")
	private BigDecimal totalFee;
}
