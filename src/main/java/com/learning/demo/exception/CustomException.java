package com.learning.demo.exception;

public class CustomException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	private Exception ex;

	public CustomException(String errorMessage, Exception ex) {
		super(errorMessage, ex);
		this.errorMessage = errorMessage;
		this.ex = ex;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Exception getEx() {
		return ex;
	}

	public void setEx(Exception ex) {
		this.ex = ex;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
