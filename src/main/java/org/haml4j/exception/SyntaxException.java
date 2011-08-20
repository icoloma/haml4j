package org.haml4j.exception;

/**
 * Triggered by an illegal or unparseable content 
 * @author icoloma
 *
 */
public class SyntaxException  extends ParseException {

	public SyntaxException(String message) {
		super(message);
	}

}
