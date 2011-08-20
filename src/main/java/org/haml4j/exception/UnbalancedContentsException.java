package org.haml4j.exception;

/**
 * Triggered by an unbalanced pair '[]', '{}', etc
 * @author icoloma
 *
 */
public class UnbalancedContentsException extends ParseException {

	public UnbalancedContentsException(String message) {
		super(message);
	}

}
