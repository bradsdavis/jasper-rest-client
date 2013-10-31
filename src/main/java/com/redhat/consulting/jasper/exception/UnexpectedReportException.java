package com.redhat.consulting.jasper.exception;

public class UnexpectedReportException extends RuntimeException {

	public UnexpectedReportException(String message, Throwable t) {
		super(message, t);
	}
}
