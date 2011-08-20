package org.haml4j.exception;

/**
 * Mixed indentation types found in the parsed file
 * @author icoloma
 *
 */
public class InconsistentIndentationException extends ParseException {

	public InconsistentIndentationException(String message) {
		super(message);
	}

}
