
package com.helloparent.fee_managment.responses;

public class CustomApiResponse<T> {

	private String message;
	private T data;
	private boolean success;

	public CustomApiResponse() {
	}

	public CustomApiResponse(String message, T data, boolean success) {
		this.message = message;
		this.data = data;
		this.success = success;
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
}
