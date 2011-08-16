package org.haml4j.parser;

public interface IndentationStrategy {

	/**
	 * Return the level of indentation
	 */
	public int getIndentationLevel(String input);
	
}
