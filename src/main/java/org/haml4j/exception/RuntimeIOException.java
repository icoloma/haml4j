package org.haml4j.exception;

import java.io.IOException;

/**
 * Unchecked IO Exception
 * @author icoloma
 *
 */
public class RuntimeIOException extends RuntimeException {

	public RuntimeIOException(IOException cause) {
		super(cause);
	}
	
}
