package org.haml4j.core;

import java.io.IOException;

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
	public HtmlWriter print(CharSequence s) throws IOException {
		delegate.append(s);
		return this;
	}

	@Override
	public void flush() throws IOException {
		// NO-OP
	}
	
	/** 
	 * Clean the String buffer
	 */
	public void reset() {
		allocate(1024);
	}

	/**
	 * Reset the buffer, allocating the expected size
	 * @param expectedSize
	 */
	public void allocate(int expectedSize) {
		delegate = new StringBuilder(1024);
	}
	
	/**
	 * @return the contents printed to this writer instance
	 */
	public String getContents() {
		return delegate.toString();
	}
}
