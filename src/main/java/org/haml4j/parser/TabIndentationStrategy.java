package org.haml4j.parser;

import org.haml4j.exception.InconsistentIndentationException;

/**
 * Use tabs for indentation
 * @author icoloma
 *
 */
public class TabIndentationStrategy implements IndentationStrategy {

	@Override
	public int getIndentationLevel(String input) {
		int i = 0;
		int length = input.length();
		int indent = 0;
		while (i < length) {
			char c = input.charAt(i++);
			if (c == '\t') {
				indent++;
			} else if (c == ' ') {
				throw new InconsistentIndentationException("Expected tabs, but found spaces");
			} else {
				break;
			}
		}
		return indent;
	}

	
	
}
