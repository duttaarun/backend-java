package com.domin.pojo;

public class ExceptionResponse {

	private String message;
	private String httpStatusCode;

	public ExceptionResponse(String message, String httpStatusCode) {
		super();
		this.message = message;
		this.httpStatusCode = httpStatusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getHttpStatusCode() {
		return httpStatusCode;
	}

	public void setHttpStatusCode(String httpStatusCode) {
		this.httpStatusCode = httpStatusCode;
	}

}
