
package com.helloparent.fee_managment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.requests.FeePaymentRequest;
import com.helloparent.fee_managment.responses.CustomApiResponse;
import com.helloparent.fee_managment.responses.FeePaymentResponse;
import com.helloparent.fee_managment.service.FeePaymentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;

@RestController
@RequestMapping("/api/payments")
@Validated
public class FeePaymentController {

	private final FeePaymentService feePaymentService;

	public FeePaymentController(FeePaymentService feePaymentService) {
		this.feePaymentService = feePaymentService;
	}

	// Record a fee payment
	@PostMapping("/recordPayment")
	public ResponseEntity<CustomApiResponse<FeePaymentResponse>> recordPayment(
			@Valid @RequestBody FeePaymentRequest request) throws ApplicationException {

		FeePaymentResponse response = feePaymentService.recordPayment(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new CustomApiResponse<>("Payment recorded successfully", response, true));
	}

	// Get payment history for a student
	@GetMapping("/getPaymentHistory/{studentId}")
	public ResponseEntity<CustomApiResponse<List<FeePaymentResponse>>> getPaymentHistory(@PathVariable Long studentId)
			throws ApplicationException {

		List<FeePaymentResponse> payments = feePaymentService.getPaymentHistory(studentId);
		return ResponseEntity.ok(new CustomApiResponse<>("Payment history fetched successfully", payments, true));
	}
}
