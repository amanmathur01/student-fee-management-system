
package com.helloparent.fee_managment.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class FeePaymentRequest {

	@NotNull(message = "Student ID is required")
	@Positive(message = "Student ID must be greater than zero")
	private Long studentId;

	@NotNull(message = "Payment amount is required")
	@DecimalMin(value = "0.01", message = "Payment amount must be greater than zero")
	private BigDecimal amount;

	@NotNull(message = "Payment date is required")
	@PastOrPresent(message = "Payment date cannot be in the future")
	private LocalDate paymentDate;

	@NotBlank(message = "Payment method is required")
	@Size(max = 30, message = "Payment method cannot exceed 30 characters")
	private String paymentMethod;

	@Size(max = 100, message = "Transaction reference cannot exceed 100 characters")
	private String transactionReference;
}
