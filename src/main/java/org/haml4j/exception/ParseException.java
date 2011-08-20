package org.haml4j.exception;

public class ParseException extends RuntimeException {

	/** the parsing line number */
	private int line = -1;
	
	public ParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParseException(String message) {
		super(message);
	}
	
	@Override
	public String getMessage() {
		return line != -1? 
				"Error parsing line " + line + ": " + super.getMessage() : 
				super.getMessage();
	}
	
	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

}
