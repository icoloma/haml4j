package org.haml4j.core;


/**
 * Writes HTML to a String
 * @author Nacho
 *
 */
public class StringHtmlWriter extends AbstractHtmlWriter {

	/** the string buffer we will use */
	private StringBuilder delegate;
	
	public StringHtmlWriter() {
		reset();
	}
	
	@Override
	public HtmlWriter print(CharSequence s) {
		delegate.append(s);
		return this;
	}

	@Override
	public void flush() {
		// NO-OP
	}
	
	/** 
	 * Clean the String buffer
	 */
	public void reset() {
		reset(1024);
	}

	/**
	 * Reset the buffer, allocating the expected size
	 * @param expectedSize
	 */
	public void reset(int expectedSize) {
		delegate = new StringBuilder(expectedSize);
	}
	
	/**
	 * @return the contents printed to this writer instance
	 */
	public String getContents() {
		return delegate.toString();
	}
}
