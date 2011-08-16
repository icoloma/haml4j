package org.haml4j.parser;

import org.haml4j.exception.InconsistentIndentationException;

/**
 * Use spaces for indentation
 * @author icoloma
 *
 */
public class SpacesIndentationStrategy implements IndentationStrategy {

	private int spacesPerIndent;
	
	public SpacesIndentationStrategy(int spacesPerIndent) {
		this.spacesPerIndent = spacesPerIndent;
	}
	
	@Override
	public int getIndentationLevel(String input) {
		int spacesCount = 0;
		int i = 0;
		int length = input.length();
		while (i < length) {
			char c = input.charAt(i++);
			if (c == ' ') {
				spacesCount++;
			} else if (c == '\t') {
				throw new InconsistentIndentationException("Expected spaces, but found tabs");
			} else {
				break;
			}
		}
		return spacesCount / spacesPerIndent;
	}	
	
}
