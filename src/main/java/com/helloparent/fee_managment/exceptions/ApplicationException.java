
package com.helloparent.fee_managment.exceptions;

import java.io.Serial;
import org.springframework.http.HttpStatus;

public class ApplicationException extends Exception {

	@Serial
	private static final long serialVersionUID = 1L;

	private final String reason;
	private final String action;
	private final HttpStatus status;

	public ApplicationException(String message) {
		super(message);
		this.reason = message;
		this.action = null;
		this.status = HttpStatus.BAD_REQUEST;
	}

	public ApplicationException(String message, String action) {
		super(message);
		this.reason = message;
		this.action = action;
		this.status = HttpStatus.BAD_REQUEST;
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
		this.reason = message;
		this.action = null;
		this.status = HttpStatus.BAD_REQUEST;
	}

	public ApplicationException(String message, String action, HttpStatus status) {
		super(message);
		this.reason = message;
		this.action = action;
		this.status = status;
	}

	public String getReason() {
		return reason;
	}

	public String getAction() {
		return action;
	}

	public HttpStatus getStatus() { return status; }
}
