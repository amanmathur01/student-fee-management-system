
package com.helloparent.fee_managment.service;

import com.helloparent.fee_managment.exceptions.ApplicationException;
import com.helloparent.fee_managment.requests.StudentRequest;
import com.helloparent.fee_managment.responses.ApiPaginationResponse;
import com.helloparent.fee_managment.responses.StudentFeeDetailsResponse;
import com.helloparent.fee_managment.responses.StudentResponse;

public interface StudentService {

	StudentResponse addStudent(StudentRequest request) throws ApplicationException;

	StudentResponse getStudentById(Long id) throws ApplicationException;

	ApiPaginationResponse<StudentResponse> searchStudents(String name, int pageNumber, int pageSize)
			throws ApplicationException;

	StudentFeeDetailsResponse getStudentFeeDetails(Long studentId) throws ApplicationException;

	void deleteStudent(Long id) throws ApplicationException;
}
