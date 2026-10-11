
package com.helloparent.fee_managment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.requests.StudentRequest;
import com.helloparent.fee_managment.responses.ApiPaginationResponse;
import com.helloparent.fee_managment.responses.CustomApiResponse;
import com.helloparent.fee_managment.responses.StudentFeeDetailsResponse;
import com.helloparent.fee_managment.responses.StudentResponse;
import com.helloparent.fee_managment.service.StudentService;

class StudentControllerTest {

	@InjectMocks
	private StudentController studentController;

	@Mock
	private StudentService studentService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		MockMvcBuilders.standaloneSetup(studentController).build();
	}

	@Test
	void testAddStudentSuccess() throws ApplicationException {

		StudentRequest request = new StudentRequest();

		StudentResponse studentResponse = new StudentResponse();
		studentResponse.setId(1L);

		when(studentService.addStudent(request)).thenReturn(studentResponse);

		ResponseEntity<CustomApiResponse<StudentResponse>> response = studentController.addStudent(request);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals("Student created successfully", response.getBody().getMessage());
		assertEquals(true, response.getBody().isSuccess());
		assertEquals(1L, response.getBody().getData().getId());

		verify(studentService, times(1)).addStudent(request);
	}

	@Test
	void testGetStudentByIdSuccess() throws ApplicationException {

		Long studentId = 1L;

		StudentResponse studentResponse = new StudentResponse();
		studentResponse.setId(studentId);

		when(studentService.getStudentById(studentId)).thenReturn(studentResponse);

		ResponseEntity<CustomApiResponse<StudentResponse>> response = studentController.getStudentById(studentId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Student details fetched successfully", response.getBody().getMessage());
		assertEquals(studentId, response.getBody().getData().getId());

		verify(studentService, times(1)).getStudentById(studentId);
	}

	@Test
	void testGetAllStudentsSuccess() throws ApplicationException {

		String studentName = "Aman";
		int pageNumber = 0;
		int pageSize = 10;

		ApiPaginationResponse<StudentResponse> paginationResponse = new ApiPaginationResponse<>();

		paginationResponse.setRecords(Collections.emptyList());

		when(studentService.searchStudents(studentName, pageNumber, pageSize)).thenReturn(paginationResponse);

		ResponseEntity<CustomApiResponse<ApiPaginationResponse<StudentResponse>>> response = studentController
				.getAllStudents(studentName, pageNumber, pageSize);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Students fetched successfully", response.getBody().getMessage());
		assertEquals(paginationResponse, response.getBody().getData());

		verify(studentService, times(1)).searchStudents(studentName, pageNumber, pageSize);
	}

	@Test
	void testGetStudentFeeDetailsSuccess() throws ApplicationException {

		Long studentId = 1L;

		StudentFeeDetailsResponse feeResponse = new StudentFeeDetailsResponse();

		when(studentService.getStudentFeeDetails(studentId)).thenReturn(feeResponse);

		ResponseEntity<CustomApiResponse<StudentFeeDetailsResponse>> response = studentController
				.getStudentFeeDetails(studentId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Student fee details fetched successfully", response.getBody().getMessage());
		assertEquals(feeResponse, response.getBody().getData());

		verify(studentService, times(1)).getStudentFeeDetails(studentId);
	}

	@Test
	void testDeleteStudentSuccess() throws ApplicationException {

		Long studentId = 1L;

		ResponseEntity<CustomApiResponse<Void>> response = studentController.deleteStudent(studentId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Student deleted successfully", response.getBody().getMessage());
		assertEquals(true, response.getBody().isSuccess());

		verify(studentService, times(1)).deleteStudent(studentId);
	}

	@Test
	void testGetStudentByIdThrowsException() throws ApplicationException {

		Long studentId = 999L;

		when(studentService.getStudentById(studentId))
				.thenThrow(new ApplicationException("Student not found with ID: " + studentId));

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> studentController.getStudentById(studentId));

		assertEquals("Student not found with ID: 999", exception.getMessage());

		verify(studentService, times(1)).getStudentById(studentId);
	}

	@Test
	void testDeleteStudentThrowsException() throws ApplicationException {

		Long studentId = 999L;

		org.mockito.Mockito.doThrow(new ApplicationException("Student not found with ID: " + studentId))
				.when(studentService).deleteStudent(studentId);

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> studentController.deleteStudent(studentId));

		assertEquals("Student not found with ID: 999", exception.getMessage());

		verify(studentService, times(1)).deleteStudent(studentId);
	}
}
