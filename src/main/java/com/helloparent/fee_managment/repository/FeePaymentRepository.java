
package com.helloparent.fee_managment.repository;

import java.util.List;
import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.helloparent.fee_managment.entity.FeePayment;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

	List<FeePayment> findByStudentIdOrderByPaymentDateDesc(Long studentId);

	@Query("SELECT COALESCE(SUM(p.amount), 0.0) FROM FeePayment p WHERE p.student.id = :studentId")
	BigDecimal getTotalPaidByStudentId(@Param("studentId") Long studentId);

	boolean existsByTransactionReference(String transactionReference);
}
