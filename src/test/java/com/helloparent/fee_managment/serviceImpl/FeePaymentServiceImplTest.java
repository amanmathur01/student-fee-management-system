package com.helloparent.fee_managment.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.helloparent.fee_managment.entity.FeePayment;
import com.helloparent.fee_managment.entity.Student;
import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.repository.FeePaymentRepository;
import com.helloparent.fee_managment.repository.StudentRepository;
import com.helloparent.fee_managment.requests.FeePaymentRequest;
import com.helloparent.fee_managment.responses.FeePaymentResponse;

class FeePaymentServiceImplTest {

	@InjectMocks
	private FeePaymentServiceImpl feePaymentService;

	@Mock
	private FeePaymentRepository feePaymentRepository;

	@Mock
	private StudentRepository studentRepository;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	private Student createStudent() {
		Student student = new Student();
		student.setId(1L);
		student.setFirstName("Aman");
		student.setLastName("Mathur");
		student.setEmail("aman@example.com");
		student.setActive(true);
		student.setTotalFee(new BigDecimal("10000.00"));
		return student;
	}

	private FeePaymentRequest createPaymentRequest() {
		FeePaymentRequest request = new FeePaymentRequest();
		request.setStudentId(1L);
		request.setAmount(new BigDecimal("5000.00"));
		request.setPaymentDate(LocalDate.of(2026, 10, 10));
		request.setPaymentMethod("UPI");
		request.setTransactionReference("TXN-1001");
		return request;
	}

	@Test
	void testRecordPaymentSuccess() throws ApplicationException {
		Student student = createStudent();
		FeePaymentRequest request = createPaymentRequest();

		when(studentRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(student));
		when(feePaymentRepository.getTotalPaidByStudentId(1L)).thenReturn(new BigDecimal("2000.00"));
		when(feePaymentRepository.existsByTransactionReference("TXN-1001")).thenReturn(false);

		when(feePaymentRepository.save(any(FeePayment.class))).thenAnswer(invocation -> {
			FeePayment payment = invocation.getArgument(0);
			payment.setId(10L);
			return payment;
		});

		FeePaymentResponse response = feePaymentService.recordPayment(request);

		assertEquals(10L, response.getId());
		assertEquals(1L, response.getStudentId());
		assertEquals(0, new BigDecimal("5000.00").compareTo(response.getAmount()));
		assertEquals(LocalDate.of(2026, 10, 10), response.getPaymentDate());
		assertEquals("UPI", response.getPaymentMethod());
		assertEquals("TXN-1001", response.getTransactionReference());

		verify(studentRepository, times(1)).findByIdForUpdate(1L);
		verify(feePaymentRepository, times(1)).getTotalPaidByStudentId(1L);
		verify(feePaymentRepository, times(1)).save(any(FeePayment.class));
	}

	@Test
	void testRecordPaymentStudentNotFound() {
		FeePaymentRequest request = createPaymentRequest();

		when(studentRepository.findByIdForUpdate(1L)).thenReturn(Optional.empty());

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> feePaymentService.recordPayment(request));

		assertEquals("Student not found with ID: 1", exception.getMessage());

		verify(feePaymentRepository, never()).save(any(FeePayment.class));
	}

	@Test
	void testRecordPaymentForInactiveStudent() {
		Student student = createStudent();
		student.setActive(false);

		FeePaymentRequest request = createPaymentRequest();

		when(studentRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(student));

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> feePaymentService.recordPayment(request));

		assertEquals("Cannot record payment for an deleted student data", exception.getMessage());

		verify(feePaymentRepository, never()).save(any(FeePayment.class));
	}

	@Test
	void testRecordPaymentExceedsOutstandingBalance() {
		Student student = createStudent();
		FeePaymentRequest request = createPaymentRequest();
		request.setAmount(new BigDecimal("9000.00"));

		when(studentRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(student));
		when(feePaymentRepository.getTotalPaidByStudentId(1L)).thenReturn(new BigDecimal("2000.00"));

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> feePaymentService.recordPayment(request));

		assertEquals("Payment amount exceeds the outstanding balance of 8000.00", exception.getMessage());

		verify(feePaymentRepository, never()).save(any(FeePayment.class));
	}

	@Test
	void testRecordPaymentDuplicateTransactionReference() {
		Student student = createStudent();
		FeePaymentRequest request = createPaymentRequest();

		when(studentRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(student));
		when(feePaymentRepository.getTotalPaidByStudentId(1L)).thenReturn(new BigDecimal("2000.00"));
		when(feePaymentRepository.existsByTransactionReference("TXN-1001")).thenReturn(true);

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> feePaymentService.recordPayment(request));

		assertEquals("A payment with this transaction reference already exists", exception.getMessage());

		verify(feePaymentRepository, never()).save(any(FeePayment.class));
	}

	@Test
	void testRecordPaymentWhenNoPreviousPaymentsExist() throws ApplicationException {
		Student student = createStudent();
		FeePaymentRequest request = createPaymentRequest();

		when(studentRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(student));
		when(feePaymentRepository.getTotalPaidByStudentId(1L)).thenReturn(null);
		when(feePaymentRepository.existsByTransactionReference("TXN-1001")).thenReturn(false);
		when(feePaymentRepository.save(any(FeePayment.class))).thenAnswer(invocation -> {
			FeePayment payment = invocation.getArgument(0);
			payment.setId(11L);
			return payment;
		});

		FeePaymentResponse response = feePaymentService.recordPayment(request);

		assertEquals(11L, response.getId());
		assertEquals(1L, response.getStudentId());

		verify(feePaymentRepository, times(1)).save(any(FeePayment.class));
	}

	@Test
	void testGetPaymentHistorySuccess() throws ApplicationException {
		FeePayment payment = new FeePayment();
		payment.setId(10L);
		payment.setStudent(createStudent());
		payment.setAmount(new BigDecimal("5000.00"));
		payment.setPaymentDate(LocalDate.of(2026, 10, 10));
		payment.setPaymentMethod("UPI");
		payment.setTransactionReference("TXN-1001");

		when(studentRepository.existsById(1L)).thenReturn(true);
		when(feePaymentRepository.findByStudentIdOrderByPaymentDateDesc(1L)).thenReturn(List.of(payment));

		List<FeePaymentResponse> response = feePaymentService.getPaymentHistory(1L);

		assertEquals(1, response.size());
		assertEquals(10L, response.get(0).getId());
		assertEquals(1L, response.get(0).getStudentId());
		assertEquals(0, new BigDecimal("5000.00").compareTo(response.get(0).getAmount()));
		assertEquals("UPI", response.get(0).getPaymentMethod());

		verify(studentRepository, times(1)).existsById(1L);
		verify(feePaymentRepository, times(1)).findByStudentIdOrderByPaymentDateDesc(1L);
	}

	@Test
	void testGetPaymentHistoryEmpty() throws ApplicationException {
		when(studentRepository.existsById(2L)).thenReturn(true);
		when(feePaymentRepository.findByStudentIdOrderByPaymentDateDesc(2L)).thenReturn(List.of());

		List<FeePaymentResponse> response = feePaymentService.getPaymentHistory(2L);

		assertEquals(0, response.size());

		verify(studentRepository, times(1)).existsById(2L);
		verify(feePaymentRepository, times(1)).findByStudentIdOrderByPaymentDateDesc(2L);
	}

	@Test
    void testGetPaymentHistoryStudentNotFound() {
        when(studentRepository.existsById(999L)).thenReturn(false);

        ApplicationException exception = assertThrows(
                ApplicationException.class,
                () -> feePaymentService.getPaymentHistory(999L));

        assertEquals("Student not found with ID: 999", exception.getMessage());

        verify(feePaymentRepository, never())
                .findByStudentIdOrderByPaymentDateDesc(999L);
    }
}