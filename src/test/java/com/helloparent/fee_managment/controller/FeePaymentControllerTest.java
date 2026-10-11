
package com.helloparent.fee_managment.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.requests.FeePaymentRequest;
import com.helloparent.fee_managment.responses.CustomApiResponse;
import com.helloparent.fee_managment.responses.FeePaymentResponse;
import com.helloparent.fee_managment.service.FeePaymentService;

class FeePaymentControllerTest {

	@InjectMocks
	private FeePaymentController feePaymentController;

	@Mock
	private FeePaymentService feePaymentService;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		MockMvcBuilders.standaloneSetup(feePaymentController).build();
	}

//	@Test
//	void testRecordPaymentSuccess() throws ApplicationException {
//
//		FeePaymentRequest request = new FeePaymentRequest();
//		request.setStudentId(1L);
//		request.setAmount(new BigDecimal("5000.00"));
//		request.setPaymentDate(LocalDate.of(2026, 10, 10));
//		request.setPaymentMethod("UPI");
//		request.setTransactionReference("TXN-1001");
//
//		FeePaymentResponse paymentResponse = new FeePaymentResponse();
//		paymentResponse.setId(1L);
//		paymentResponse.setStudentId(1L);
//		request.setAmount(new BigDecimal("5000.00"));
//		paymentResponse.setPaymentDate(LocalDate.of(2026, 10, 10));
//		paymentResponse.setPaymentMethod("UPI");
//		paymentResponse.setTransactionReference("TXN-1001");
//
//		when(feePaymentService.recordPayment(request)).thenReturn(paymentResponse);
//
//		ResponseEntity<CustomApiResponse<FeePaymentResponse>> response = feePaymentController.recordPayment(request);
//
//		assertEquals(HttpStatus.CREATED, response.getStatusCode());
//		assertEquals("Payment recorded successfully", response.getBody().getMessage());
//		assertEquals(true, response.getBody().isSuccess());
//		assertEquals(1L, response.getBody().getData().getId());
//		assertEquals(5000.0, response.getBody().getData().getAmount());
//
//		verify(feePaymentService, times(1)).recordPayment(request);
//	}

	@Test
	void testRecordPaymentThrowsException() throws ApplicationException {

		FeePaymentRequest request = new FeePaymentRequest();
		request.setStudentId(999L);
		request.setAmount(new BigDecimal("5000.00"));

		when(feePaymentService.recordPayment(request)).thenThrow(new ApplicationException("Student not found"));

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> feePaymentController.recordPayment(request));

		assertEquals("Student not found", exception.getMessage());

		verify(feePaymentService, times(1)).recordPayment(request);
	}

	@Test
	void testGetPaymentHistoryEmpty() throws ApplicationException {

		Long studentId = 2L;

		when(feePaymentService.getPaymentHistory(studentId)).thenReturn(Collections.emptyList());

		ResponseEntity<CustomApiResponse<List<FeePaymentResponse>>> response = feePaymentController
				.getPaymentHistory(studentId);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(0, response.getBody().getData().size());

		verify(feePaymentService, times(1)).getPaymentHistory(studentId);
	}

	@Test
	void testGetPaymentHistoryThrowsException() throws ApplicationException {

		Long studentId = 999L;

		when(feePaymentService.getPaymentHistory(studentId))
				.thenThrow(new ApplicationException("Student not found with ID: " + studentId));

		ApplicationException exception = assertThrows(ApplicationException.class,
				() -> feePaymentController.getPaymentHistory(studentId));

		assertEquals("Student not found with ID: 999", exception.getMessage());

		verify(feePaymentService, times(1)).getPaymentHistory(studentId);
	}
}
