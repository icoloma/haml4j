package org.haml4j.parser;


/**
 * Splits the input into lines
 * @author icoloma
 *
 */
public class InputLines {

	/** the list of lines to process */
	private String input;
	
	/** the current row, starting at 1 */
	private int row = 1;

	/** the current offset inside input */
	private int offset = 0;
	
	/** the last returned line, null if not yet there */
	private String lastLine;
	
	/** use tabs or spaces for indentation */
	private IndentationStrategy strategy;
	
	public InputLines(String input) {
		this.input = input;
	}

	/**
	 * @return the next line to process, handling multiline if needed. 
	 * Null if EOF has been reached
	 */
	public String nextLine() {
		int length = input.length();
		if (offset >= length) {
			return null;
		}
		int crpos = input.indexOf('\n', offset);
		if (crpos == -1) {
			crpos = length;
		}
		row++;
		if (input.charAt(crpos - 1) != ParserConstants.MULTILINE_CHAR_VALUE) {
			lastLine = input.substring(offset, crpos);
			offset = crpos + 1;
			return lastLine;
		}
		
		StringBuilder sb = new StringBuilder(200);
		while (true) {
			if (offset == length) {
				break;
			}
			if (input.charAt(crpos - 1) != ParserConstants.MULTILINE_CHAR_VALUE) {
				row--;
				break;
			}
			int rtrimpos = crpos - 2;
			while (rtrimpos > offset && input.charAt(rtrimpos) == ' ') {
				rtrimpos--;
			}
			sb.append(input.substring(offset, rtrimpos + 1)).append("\n");
			offset = crpos + 1;
			crpos = input.indexOf('\n', offset);
			if (crpos == -1) {
				crpos = length;
			}
			row++;
		}
		lastLine = sb.toString();
		return lastLine;
	}

	public int getRow() {
		return row;
	}
	
	/**
	 * @return the indentation level of the last returned line
	 */
	public int getCurrentIndentation() {
		if (strategy == null) {
			if (lastLine.length() == 0) {
				return 0;
			}
			if (lastLine.charAt(0) == '\t') {
				strategy = new TabIndentationStrategy();
			} else if (lastLine.charAt(0) == ' ') {
				strategy = new SpacesIndentationStrategy(2);
			} else {
				return 0;
			}
		}
		return strategy.getIndentationLevel(lastLine);
	}
	
}
