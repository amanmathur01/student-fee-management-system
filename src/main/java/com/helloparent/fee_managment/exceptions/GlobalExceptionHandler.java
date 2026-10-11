
package com.helloparent.fee_managment.exceptions;

import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.helloparent.fee_managment.responses.CustomApiResponse;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	// Handle business exceptions
	@ExceptionHandler(ApplicationException.class)
	public ResponseEntity<CustomApiResponse<Void>> handleApplicationException(ApplicationException ex) {

		CustomApiResponse<Void> response = new CustomApiResponse<>(ex.getMessage(), null, false);

		return ResponseEntity.status(ex.getStatus()).body(response);
	}

	@ExceptionHandler({ HandlerMethodValidationException.class, HttpMessageNotReadableException.class })
	public ResponseEntity<CustomApiResponse<Void>> handleMalformedRequest(Exception ex) {
		return ResponseEntity.badRequest()
				.body(new CustomApiResponse<>("Request contains invalid or missing values", null, false));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<CustomApiResponse<String>> handleConstraintViolation(ConstraintViolationException ex) {
		String errors = ex.getConstraintViolations().stream().map(violation -> violation.getMessage()).distinct()
				.collect(Collectors.joining("; "));
		return ResponseEntity.badRequest().body(new CustomApiResponse<>("Request validation failed", errors, false));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<CustomApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(new CustomApiResponse<>("The request conflicts with existing data", null, false));
	}

	// Handle request body validation errors
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CustomApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {

		String errorMessage = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage())
				.filter(message -> message != null && !message.isBlank()).findFirst()
				.orElse("Request validation failed");

		CustomApiResponse<Void> response = new CustomApiResponse<>(errorMessage, null, false);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}

	// Handle unexpected exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<CustomApiResponse<Void>> handleGeneralException(Exception ex) {

		CustomApiResponse<Void> response = new CustomApiResponse<>("An unexpected error occurred.", null, false);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
}
