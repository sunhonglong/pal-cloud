package com.pal.cloud.common.exception;

public class ApiException extends RuntimeException {
 
	private static final long serialVersionUID = 1L;
	
	private int code;
	private String message;

	public ApiException(String message, int code) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	

}
