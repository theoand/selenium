package com.andreouconsulting.theo.selenium;

/**
 * Exception to be thrown for unsupported actions.
 * 
 * @author theo@andreouconsulting.com
 *
 */
class UnsupportedException extends Exception {

	private static final long serialVersionUID = 407542547277463364L;

	public UnsupportedException(String message) {
		super(message);
	}

}
