package com.smt.jbpm;

/**
 * 
 * @author DougLei
 */
public class SmtJbpmException extends RuntimeException {

	public SmtJbpmException() {}
	public SmtJbpmException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	public SmtJbpmException(String message, Throwable cause) {
		super(message, cause);
	}
	public SmtJbpmException(String message) {
		super(message);
	}
	public SmtJbpmException(Throwable cause) {
		super(cause);
	}
}
