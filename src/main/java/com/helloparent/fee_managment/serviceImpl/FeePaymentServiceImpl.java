
package com.helloparent.fee_managment.serviceImpl;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;

import com.helloparent.fee_managment.entity.FeePayment;
import com.helloparent.fee_managment.entity.Student;
import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.repository.FeePaymentRepository;
import com.helloparent.fee_managment.repository.StudentRepository;
import com.helloparent.fee_managment.requests.FeePaymentRequest;
import com.helloparent.fee_managment.responses.FeePaymentResponse;
import com.helloparent.fee_managment.service.FeePaymentService;

@Service
public class FeePaymentServiceImpl implements FeePaymentService {

	private final FeePaymentRepository feePaymentRepository;
	private final StudentRepository studentRepository;

	public FeePaymentServiceImpl(FeePaymentRepository feePaymentRepository, StudentRepository studentRepository) {
		this.feePaymentRepository = feePaymentRepository;
		this.studentRepository = studentRepository;
	}

	@Override
	@Transactional
	public FeePaymentResponse recordPayment(FeePaymentRequest request) throws ApplicationException {

		Student student = studentRepository.findByIdForUpdate(request.getStudentId())
				.orElseThrow(() -> new ApplicationException("Student not found with ID: " + request.getStudentId(),
						"recordPayment", HttpStatus.NOT_FOUND));

		if (!student.isActive()) {
			throw new ApplicationException("Cannot record payment for an deleted student data", "recordPayment", HttpStatus.CONFLICT);
		}

		BigDecimal totalPaid = feePaymentRepository.getTotalPaidByStudentId(student.getId());
		if (totalPaid == null) 
			totalPaid = BigDecimal.ZERO;
		
		BigDecimal outstandingBalance = student.getTotalFee().subtract(totalPaid);

		if (request.getAmount().compareTo(outstandingBalance) > 0) {
			throw new ApplicationException("Payment amount exceeds the outstanding balance of " + outstandingBalance,
					"recordPayment", HttpStatus.CONFLICT);
		}

		String transactionReference = trimToNull(request.getTransactionReference());
		if (transactionReference != null && feePaymentRepository.existsByTransactionReference(transactionReference)) {
			throw new ApplicationException("A payment with this transaction reference already exists", "recordPayment", HttpStatus.CONFLICT);
		}

		FeePayment payment = new FeePayment();
		payment.setStudent(student);
		payment.setAmount(request.getAmount());
		payment.setPaymentDate(request.getPaymentDate());
		payment.setPaymentMethod(request.getPaymentMethod().trim());
		payment.setTransactionReference(transactionReference);

		FeePayment savedPayment = feePaymentRepository.save(payment);

		return convertToResponse(savedPayment);
	}

	@Override
	@Transactional
	public List<FeePaymentResponse> getPaymentHistory(Long studentId) throws ApplicationException {

		if (!studentRepository.existsById(studentId)) {
			throw new ApplicationException("Student not found with ID: " + studentId, "getPaymentHistory", HttpStatus.NOT_FOUND);
		}

		List<FeePayment> payments = feePaymentRepository.findByStudentIdOrderByPaymentDateDesc(studentId);

		return payments.stream().map(this::convertToResponse).toList();
	}

	private FeePaymentResponse convertToResponse(FeePayment payment) {

		FeePaymentResponse response = new FeePaymentResponse();

		response.setId(payment.getId());
		response.setStudentId(payment.getStudent().getId());
		response.setAmount(payment.getAmount());
		response.setPaymentDate(payment.getPaymentDate());
		response.setPaymentMethod(payment.getPaymentMethod());
		response.setTransactionReference(payment.getTransactionReference());

		return response;
	}

	private String trimToNull(String value) {
		if (value == null) return null;
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}
}
