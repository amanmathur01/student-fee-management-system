
package com.helloparent.fee_managment.serviceImpl;

import java.util.List;
import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;

import com.helloparent.fee_managment.entity.FeePayment;
import com.helloparent.fee_managment.entity.Student;
import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.repository.FeePaymentRepository;
import com.helloparent.fee_managment.repository.StudentRepository;
import com.helloparent.fee_managment.requests.StudentRequest;
import com.helloparent.fee_managment.responses.ApiPaginationResponse;
import com.helloparent.fee_managment.responses.FeePaymentResponse;
import com.helloparent.fee_managment.responses.StudentFeeDetailsResponse;
import com.helloparent.fee_managment.responses.StudentResponse;
import com.helloparent.fee_managment.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {

	private final StudentRepository studentRepository;
	private final FeePaymentRepository feePaymentRepository;

	public StudentServiceImpl(StudentRepository studentRepository, FeePaymentRepository feePaymentRepository) {
		this.studentRepository = studentRepository;
		this.feePaymentRepository = feePaymentRepository;
	}

	// Register a student
	@Override
	@Transactional
	public StudentResponse addStudent(StudentRequest request) throws ApplicationException {

		String email = request.getEmail().trim().toLowerCase();

		if (studentRepository.existsByEmail(email)) {
			throw new ApplicationException("Student with this email already exists");
		}

		Student student = new Student();

		student.setFirstName(request.getFirstName().trim());
		student.setLastName(request.getLastName().trim());
		student.setDateOfBirth(request.getDateOfBirth());
		student.setGender(request.getGender());

		student.setEmail(email);
		student.setPhone(trimToNull(request.getPhone()));

		student.setFatherName(request.getFatherName().trim());
		student.setMotherName(trimToNull(request.getMotherName()));

		student.setAddress(request.getAddress().trim());
		student.setCity(request.getCity().trim());
		student.setState(request.getState().trim());
		student.setPostalCode(request.getPostalCode().trim());

		student.setClassName(request.getClassName().trim());
		student.setSection(trimToNull(request.getSection()));

		student.setTotalFee(request.getTotalFee());
		student.setActive(true);

		Student savedStudent = studentRepository.save(student);

		return mapToStudentResponse(savedStudent);
	}

	// Fetch student by ID
	@Override
	@Transactional(readOnly = true)
	public StudentResponse getStudentById(Long id) throws ApplicationException {

		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new ApplicationException("Student does not exist with ID: " + id, "getStudentById", HttpStatus.NOT_FOUND));

		return mapToStudentResponse(student);
	}

	// Search and paginate active students
	@Override
	@Transactional(readOnly = true)
	public ApiPaginationResponse<StudentResponse> searchStudents(String name, int pageNumber, int pageSize)
			throws ApplicationException {

		if (pageNumber < 0 || pageSize < 1 || pageSize > 100) {
			throw new ApplicationException("Page number must be >= 0 and page size must be between 1 and 100",
					"searchStudents");
		}

		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		Page<Student> studentPage;

		if (name == null || name.isBlank()) {
			studentPage = studentRepository.findByActiveTrue(pageable);
		} else {
			studentPage = studentRepository.searchActiveStudentsByName(name.trim(), pageable);
		}

		List<StudentResponse> records = studentPage.getContent().stream().map(this::mapToStudentResponse).toList();

		ApiPaginationResponse<StudentResponse> response = new ApiPaginationResponse<>();

		response.setPageSize(studentPage.getSize());
		response.setPageNumber(studentPage.getNumber());
		response.setTotalPages(studentPage.getTotalPages());
		response.setFirst(studentPage.isFirst());
		response.setLast(studentPage.isLast());
		response.setTotalRecords(studentPage.getTotalElements());
		response.setRecords(records);

		return response;
	}

	// Fetch student fee details and payment history
	@Override
	@Transactional(readOnly = true)
	public StudentFeeDetailsResponse getStudentFeeDetails(Long studentId) throws ApplicationException {

		Student student = studentRepository.findById(studentId).orElseThrow(
			() -> new ApplicationException("Student not found with ID: " + studentId, "getStudentFeeDetails", HttpStatus.NOT_FOUND));

		List<FeePayment> payments = feePaymentRepository.findByStudentIdOrderByPaymentDateDesc(studentId);

		BigDecimal totalPaid = feePaymentRepository.getTotalPaidByStudentId(studentId);
		if (totalPaid == null) 
			totalPaid = BigDecimal.ZERO;

		StudentFeeDetailsResponse response = new StudentFeeDetailsResponse();

		response.setStudent(mapToStudentResponse(student));
		response.setTotalPaid(totalPaid);
		response.setOutstandingBalance(student.getTotalFee().subtract(totalPaid));

		response.setPaymentHistory(payments.stream().map(this::mapToFeePaymentResponse).toList());

		return response;
	}

	// Soft-delete: deactivate the student
	@Override
	@Transactional
	public void deleteStudent(Long id) throws ApplicationException {

		Student student = studentRepository.findById(id)
				.orElseThrow(() -> new ApplicationException("Student not found with ID: " + id, "deleteStudent", HttpStatus.NOT_FOUND));

		if (!student.isActive()) {
			throw new ApplicationException("Student is already inactive", "deleteStudent", HttpStatus.CONFLICT);
		}

		student.setActive(false);
		studentRepository.save(student);
	}

	private StudentResponse mapToStudentResponse(Student student) {

		StudentResponse response = new StudentResponse();

		response.setId(student.getId());

		response.setFirstName(student.getFirstName());
		response.setLastName(student.getLastName());
		response.setDateOfBirth(student.getDateOfBirth());
		response.setGender(student.getGender());

		response.setEmail(student.getEmail());
		response.setPhone(student.getPhone());

		response.setFatherName(student.getFatherName());
		response.setMotherName(student.getMotherName());

		response.setAddress(student.getAddress());
		response.setCity(student.getCity());
		response.setState(student.getState());
		response.setPostalCode(student.getPostalCode());

		response.setClassName(student.getClassName());
		response.setSection(student.getSection());

		response.setTotalFee(student.getTotalFee());
		response.setCreatedAt(student.getCreatedAt());
		response.setActive(student.isActive());

		return response;
	}

	private FeePaymentResponse mapToFeePaymentResponse(FeePayment payment) {

		FeePaymentResponse response = new FeePaymentResponse();

		// Payment details
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
