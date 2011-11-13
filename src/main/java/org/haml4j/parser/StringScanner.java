package org.haml4j.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Naive implementation of basic ruby StringScanner methods in Java
 * {@link Matcher} just did not cut it. 
 * @author icoloma
 *
 */
public class StringScanner {

	/** the input String */
	private String input;

	/** the starting index */
	private int index;
	
	public StringScanner(String input) {
		this.input = input;
	}
	
	/**
	 * Finds the next occurrence of this pattern and returns it, incrementing the current index. 
	 * If there is no immediate sequence, returns null (this is the difference with Matcher). 
	 * @param pattern
	 * @return
	 */
	public String scan(Pattern pattern) {
		Matcher matcher = pattern.matcher(input);
		if (matcher.find(index) && matcher.start() == index) {
			index = matcher.end();
			return matcher.group();
		}
		return null;
	}
	
	public boolean eos() {
		return index == input.length();
	}
	
}
