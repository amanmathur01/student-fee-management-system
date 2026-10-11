package com.helloparent.fee_managment.service;

import java.util.List;

import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.requests.FeePaymentRequest;
import com.helloparent.fee_managment.responses.FeePaymentResponse;

public interface FeePaymentService {

	FeePaymentResponse recordPayment(FeePaymentRequest request) throws ApplicationException;

	List<FeePaymentResponse> getPaymentHistory(Long studentId) throws ApplicationException;
}
