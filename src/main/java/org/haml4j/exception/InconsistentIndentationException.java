package org.haml4j.exception;

/**
 * Mixed indentation types found in the parsed file
 * @author icoloma
 *
 */
public class InconsistentIndentationException extends HamlParsingException {

	public InconsistentIndentationException(String message) {
		super(message);
	}

}
