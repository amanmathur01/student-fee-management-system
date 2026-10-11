
package com.helloparent.fee_managment.responses;

public class ApiResponse<T> {

	private String message;
	private T data;
	private boolean success;
	private String statusCode;

	public ApiResponse() {
	}

	public ApiResponse(String message, T data, boolean success, String statusCode) {
		this.message = message;
		this.data = data;
		this.success = success;
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
}
