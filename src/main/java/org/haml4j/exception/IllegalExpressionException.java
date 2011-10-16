package org.haml4j.exception;

/**
 * An Illegal expression has been used for an attribute
 * @author icoloma
 *
 */
public class IllegalExpressionException extends RuntimeException {

	public IllegalExpressionException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalExpressionException(String message) {
		super(message);
	}

	public IllegalExpressionException(Throwable cause) {
		super(cause);
	}

}
