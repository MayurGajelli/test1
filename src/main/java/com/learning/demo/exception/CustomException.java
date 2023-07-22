package com.learning.demo.exception;

public class CustomException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomException(String errorMessage, Throwable ex) {
		super(errorMessage, ex);
	}
}
