
package com.helloparent.fee_managment.responses;

import java.util.List;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentFeeDetailsResponse {

	private StudentResponse student;
	private BigDecimal totalPaid;
	private BigDecimal outstandingBalance;
	private List<FeePaymentResponse> paymentHistory;
}
