
package com.helloparent.fee_managment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.requests.StudentRequest;
import com.helloparent.fee_managment.responses.ApiPaginationResponse;
import com.helloparent.fee_managment.responses.CustomApiResponse;
import com.helloparent.fee_managment.responses.StudentFeeDetailsResponse;
import com.helloparent.fee_managment.responses.StudentResponse;
import com.helloparent.fee_managment.service.StudentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/students")
@Validated
public class StudentController {

	private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

	private final StudentService studentService;

	public StudentController(StudentService studentService) {
		this.studentService = studentService;
	}

	// Register a new student
	@PostMapping("/register")
	public ResponseEntity<CustomApiResponse<StudentResponse>> addStudent(@Valid @RequestBody StudentRequest request)
			throws ApplicationException {

		logger.info("Request received to create a student");

		StudentResponse response = studentService.addStudent(request);

		logger.info("Student created successfully with ID: {}", response.getId());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new CustomApiResponse<>("Student created successfully", response, true));
	}

	// Get student detials by ID
	@GetMapping("/getById/{id}")
	public ResponseEntity<CustomApiResponse<StudentResponse>> getStudentById(@PathVariable Long id)
			throws ApplicationException {

		logger.info("Fetching student with ID: {}", id);

		StudentResponse response = studentService.getStudentById(id);

		return ResponseEntity.ok(new CustomApiResponse<>("Student details fetched successfully", response, true));
	}

	// Get all active students or search by name with pagination
	@GetMapping("/getAllStudents")
	public ResponseEntity<CustomApiResponse<ApiPaginationResponse<StudentResponse>>> getAllStudents(
			@RequestParam(required = false) String studentName,
			@RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number must be at least 0") int pageNumber,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") int pageSize)
			throws ApplicationException {

		logger.info("Fetching students. Name: {}, Page: {}, Size: {}", studentName, pageNumber, pageSize);

		ApiPaginationResponse<StudentResponse> response = studentService.searchStudents(studentName, pageNumber,
				pageSize);

		return ResponseEntity.ok(new CustomApiResponse<>("Students fetched successfully", response, true));
	}

	// Get student fee details
	@GetMapping("/fees/{id}")
	public ResponseEntity<CustomApiResponse<StudentFeeDetailsResponse>> getStudentFeeDetails(@PathVariable Long id)
			throws ApplicationException {

		logger.info("Fetching fee details for student ID: {}", id);

		StudentFeeDetailsResponse response = studentService.getStudentFeeDetails(id);

		return ResponseEntity.ok(new CustomApiResponse<>("Student fee details fetched successfully", response, true));
	}

	// Delete a student who has left the school
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<CustomApiResponse<Void>> deleteStudent(@PathVariable Long id) throws ApplicationException {

		logger.info("Request received to delete student with ID: {}", id);

		studentService.deleteStudent(id);

		logger.info("Student deleted successfully. ID: {}", id);

		return ResponseEntity.ok(new CustomApiResponse<>("Student deleted successfully", null, true));
	}
}
