package com.smt.jbpm;

/**
 * 
 * @author DougLei
 */
public class ProcessWebException extends RuntimeException {

	public ProcessWebException() {}
	public ProcessWebException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public ProcessWebException(String message, Throwable cause) {
		super(message, cause);
	}
	public ProcessWebException(String message) {
		super(message);
	}
	public ProcessWebException(Throwable cause) {
		super(cause);
	}
}
