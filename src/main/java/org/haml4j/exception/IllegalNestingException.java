package org.haml4j.exception;

/**
 * Triggered by an illegal nesting of tags 
 * @author icoloma
 *
 */
public class IllegalNestingException  extends ParseException {

	public IllegalNestingException(String message) {
		super(message);
	}

}
