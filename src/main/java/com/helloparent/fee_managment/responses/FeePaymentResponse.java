
package com.helloparent.fee_managment.responses;

import java.time.LocalDate;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeePaymentResponse {

	// Payment details
	private Long id;
	private Long studentId;
	private BigDecimal amount;
	private LocalDate paymentDate;
	private String paymentMethod;
	private String transactionReference;
}
