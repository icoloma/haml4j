package org.haml4j.exception;

/**
 * Error executing a compiled  Expression
 * @author icoloma
 *
 */
public class ExpressionEvaluationException extends RuntimeException {

	public ExpressionEvaluationException(Throwable cause) {
		super(cause);
	}

}
