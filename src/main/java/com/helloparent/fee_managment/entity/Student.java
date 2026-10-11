
package com.helloparent.fee_managment.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.helloparent.fee_managment.enums.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Personal details
	@Column(name = "first_name", nullable = false, length = 50)
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 50)
	private String lastName;

	@Column(name = "date_of_birth", nullable = false)
	private LocalDate dateOfBirth;

	@Enumerated(EnumType.ORDINAL)
	@Column(nullable = false, length = 10)
	private Gender gender;

	// Contact details
	@Column(nullable = false, unique = true, length = 150)
	private String email;

	@Column(length = 10)
	private String phone;

	// Parent details
	@Column(name = "father_name", nullable = false, length = 100)
	private String fatherName;

	@Column(name = "mother_name", length = 100)
	private String motherName;

	// Address details
	@Column(nullable = false, length = 250)
	private String address;

	@Column(nullable = false, length = 100)
	private String city;

	@Column(nullable = false, length = 100)
	private String state;

	@Column(name = "postal_code", nullable = false, length = 6)
	private String postalCode;

	// Academic details
	@Column(name = "class_name", nullable = false, length = 50)
	private String className;

	@Column(length = 20)
	private String section;

	// Fee details
	@Column(name = "total_fee", nullable = false, precision = 12, scale = 2)
	private BigDecimal totalFee;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// Soft delete
	@Column(nullable = false)
	private boolean active = true;

	@OneToMany(mappedBy = "student", fetch = FetchType.LAZY)
	private List<FeePayment> payments = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}
}
