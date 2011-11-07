package org.haml4j.model;

import org.haml4j.script.ExpressionLanguageEngine;

/**
 * A tag attribute
 * @author icoloma
 *
 */
public class Attribute {

	/** attribute name */
	private String name;
	
	/** true to evaluate using the {@link ExpressionLanguageEngine} */
	private boolean evaluate;
	
	/** attribute value */
	private String value;
	
	
	
}
