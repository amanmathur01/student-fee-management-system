package com.helloparent.fee_managment.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.helloparent.fee_managment.entity.FeePayment;
import com.helloparent.fee_managment.entity.Student;
import com.helloparent.fee_managment.enums.Gender;
import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.repository.FeePaymentRepository;
import com.helloparent.fee_managment.repository.StudentRepository;
import com.helloparent.fee_managment.requests.StudentRequest;
import com.helloparent.fee_managment.responses.ApiPaginationResponse;
import com.helloparent.fee_managment.responses.StudentFeeDetailsResponse;
import com.helloparent.fee_managment.responses.StudentResponse;

class StudentServiceImplTest {

	@InjectMocks
	private StudentServiceImpl studentService;

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private FeePaymentRepository feePaymentRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private Student createStudent() {
		Student student = new Student();
		student.setId(1L);
		student.setFirstName("Aman");
		student.setLastName("Mathur");
		student.setDateOfBirth(LocalDate.of(2000, 1, 15));
		student.setGender(Gender.MALE);
		student.setEmail("aman@example.com");
		student.setPhone("9876543210");
		student.setFatherName("Rajesh Mathur");
		student.setMotherName("Sunita Mathur");
		student.setAddress("Sector 62");
		student.setCity("Noida");
		student.setState("Uttar Pradesh");
		student.setPostalCode("201309");
		student.setClassName("10");
		student.setSection("A");
		student.setTotalFee(new BigDecimal("10000.00"));
		student.setCreatedAt(LocalDateTime.now());
		student.setActive(true);
		return student;
	}

	private StudentRequest createStudentRequest() {
		StudentRequest request = new StudentRequest();
		request.setFirstName("Aman");
		request.setLastName("Mathur");
		request.setDateOfBirth(LocalDate.of(2000, 1, 15));
		request.setGender(Gender.MALE);
		request.setEmail("AMAN@EXAMPLE.COM");
		request.setPhone("9876543210");
		request.setFatherName("Rajesh Mathur");
		request.setMotherName("Sunita Mathur");
		request.setAddress("Sector 62");
		request.setCity("Noida");
		request.setState("Uttar Pradesh");
		request.setPostalCode("201309");
		request.setClassName("10");
		request.setSection("A");
		request.setTotalFee(new BigDecimal("10000.00"));
		return request;
	}

	@Test
	void testAddStudentSuccess() throws ApplicationException {
		StudentRequest request = createStudentRequest();
		Student student = createStudent();

		when(studentRepository.existsByEmail("aman@example.com")).thenReturn(false);
		when(studentRepository.save(any(Student.class))).thenReturn(student);

		StudentResponse response = studentService.addStudent(request);

		assertEquals(1L, response.getId());
		assertEquals("Aman", response.getFirstName());
		assertEquals("Mathur", response.getLastName());
		assertEquals("aman@example.com", response.getEmail());
		assertEquals(true, response.isActive());

		verify(studentRepository, times(1)).existsByEmail("aman@example.com");
		verify(studentRepository, times(1)).save(any(Student.class));
	}

	@Test
	void testAddStudentDuplicateEmail() {
		StudentRequest request = createStudentRequest();

		when(studentRepository.existsByEmail("aman@example.com")).thenReturn(true);

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> studentService.addStudent(request));

		assertEquals("Student with this email already exists", exception.getMessage());

		verify(studentRepository, never()).save(any(Student.class));
	}

	@Test
	void testGetStudentByIdSuccess() throws ApplicationException {
		Student student = createStudent();

		when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

		StudentResponse response = studentService.getStudentById(1L);

		assertEquals(1L, response.getId());
		assertEquals("Aman", response.getFirstName());
		assertEquals("aman@example.com", response.getEmail());

		verify(studentRepository, times(1)).findById(1L);
	}

	@Test
	void testGetStudentByIdNotFound() {
		when(studentRepository.findById(99L)).thenReturn(Optional.empty());

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> studentService.getStudentById(99L));

		assertEquals("Student does not exist with ID: 99", exception.getMessage());

		verify(studentRepository, times(1)).findById(99L);
	}

	@Test
	void testSearchStudentsWithoutName() throws ApplicationException {
		Student student = createStudent();
		Page<Student> studentPage = new PageImpl<>(List.of(student));

		when(studentRepository.findByActiveTrue(any(Pageable.class))).thenReturn(studentPage);

		ApiPaginationResponse<StudentResponse> response = studentService.searchStudents(null, 0, 10);

		assertEquals(1, response.getRecords().size());
		assertEquals("Aman", response.getRecords().get(0).getFirstName());
		assertEquals(0, response.getPageNumber());
		assertEquals(1, response.getPageSize());
		assertEquals(1, response.getTotalRecords());

		verify(studentRepository, times(1)).findByActiveTrue(any(Pageable.class));
		verify(studentRepository, never()).searchActiveStudentsByName(any(), any(Pageable.class));
	}

	@Test
	void testSearchStudentsWithName() throws ApplicationException {
		Student student = createStudent();
		Page<Student> studentPage = new PageImpl<>(List.of(student));

		when(studentRepository.searchActiveStudentsByName(eq("Aman"), any(Pageable.class))).thenReturn(studentPage);

		ApiPaginationResponse<StudentResponse> response = studentService.searchStudents(" Aman ", 0, 10);

		assertEquals(1, response.getRecords().size());
		assertEquals("Aman", response.getRecords().get(0).getFirstName());

		verify(studentRepository, times(1)).searchActiveStudentsByName(eq("Aman"), any(Pageable.class));
	}

	@Test
	void testSearchStudentsInvalidPageNumber() {
		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> studentService.searchStudents(null, -1, 10));

		assertEquals("Page number must be >= 0 and page size must be between 1 and 100", exception.getMessage());

		verify(studentRepository, never()).findByActiveTrue(any(Pageable.class));
	}

	@Test
	void testSearchStudentsInvalidPageSize() {
		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> studentService.searchStudents(null, 0, 101));

		assertEquals("Page number must be >= 0 and page size must be between 1 and 100", exception.getMessage());

		verify(studentRepository, never()).findByActiveTrue(any(Pageable.class));
	}

	@Test
	void testGetStudentFeeDetailsSuccess() throws ApplicationException {
		Student student = createStudent();

		FeePayment payment = new FeePayment();
		payment.setId(10L);
		payment.setStudent(student);
		payment.setAmount(new BigDecimal("3000.00"));
		payment.setPaymentDate(LocalDate.of(2026, 10, 10));
		payment.setPaymentMethod("UPI");
		payment.setTransactionReference("TXN-1001");

		when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
		when(feePaymentRepository.findByStudentIdOrderByPaymentDateDesc(1L)).thenReturn(List.of(payment));
		when(feePaymentRepository.getTotalPaidByStudentId(1L)).thenReturn(new BigDecimal("3000.00"));

		StudentFeeDetailsResponse response = studentService.getStudentFeeDetails(1L);

		assertEquals(1L, response.getStudent().getId());
		assertEquals(0, new BigDecimal("3000.00").compareTo(response.getTotalPaid()));
		assertEquals(0, new BigDecimal("7000.00").compareTo(response.getOutstandingBalance()));
		assertEquals(1, response.getPaymentHistory().size());
		assertEquals(10L, response.getPaymentHistory().get(0).getId());

		verify(studentRepository, times(1)).findById(1L);
		verify(feePaymentRepository, times(1)).findByStudentIdOrderByPaymentDateDesc(1L);
		verify(feePaymentRepository, times(1)).getTotalPaidByStudentId(1L);
	}

	@Test
	void testGetStudentFeeDetailsNoPayments() throws ApplicationException {
		Student student = createStudent();

		when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
		when(feePaymentRepository.findByStudentIdOrderByPaymentDateDesc(1L)).thenReturn(List.of());
		when(feePaymentRepository.getTotalPaidByStudentId(1L)).thenReturn(null);

		StudentFeeDetailsResponse response = studentService.getStudentFeeDetails(1L);

		assertEquals(0, BigDecimal.ZERO.compareTo(response.getTotalPaid()));
		assertEquals(0, new BigDecimal("10000.00").compareTo(response.getOutstandingBalance()));
		assertEquals(0, response.getPaymentHistory().size());
	}

	@Test
	void testGetStudentFeeDetailsStudentNotFound() {
		when(studentRepository.findById(99L)).thenReturn(Optional.empty());

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> studentService.getStudentFeeDetails(99L));

		assertEquals("Student not found with ID: 99", exception.getMessage());

		verify(feePaymentRepository, never()).findByStudentIdOrderByPaymentDateDesc(99L);
	}

	@Test
	void testDeleteStudentSuccess() throws ApplicationException {
		Student student = createStudent();

		when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
		when(studentRepository.save(any(Student.class))).thenReturn(student);

		studentService.deleteStudent(1L);

		assertEquals(false, student.isActive());

		verify(studentRepository, times(1)).findById(1L);
		verify(studentRepository, times(1)).save(student);
	}

	@Test
	void testDeleteStudentNotFound() {
		when(studentRepository.findById(99L)).thenReturn(Optional.empty());

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> studentService.deleteStudent(99L));

		assertEquals("Student not found with ID: 99", exception.getMessage());

		verify(studentRepository, never()).save(any(Student.class));
	}

	@Test
	void testDeleteStudentAlreadyInactive() {
		Student student = createStudent();
		student.setActive(false);

		when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> studentService.deleteStudent(1L));

		assertEquals("Student is already inactive", exception.getMessage());

		verify(studentRepository, never()).save(any(Student.class));
	}
}