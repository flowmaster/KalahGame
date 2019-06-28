/**
 * This is a custom exception which raised to identify specific operation that user trying to do and not allowed to have.
 * So by this we notify consumer that an appropriate message with specific status code and helps to perform frontend 
 * eligible operation
 * @author Sambed
 * @date 23/05/2019
 * @date last update - 
 * @change by - 
 */

package com.bb.kalah.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class KalahException extends RuntimeException  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is a custom exception constructor invokes the Exception class but handle with predefine message .
	 * @param exception
	 */
	public KalahException(String exception) {
		super(exception);
	}

}
