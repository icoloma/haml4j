package org.haml4j.core;

/**
 * Used to execute a HAML template that will be rendered into a String
 * @author icoloma
 *
 */
public class StringBackedContext extends AbstractContext {

	private StringHtmlWriter writer = new StringHtmlWriter();

	@Override
	public HtmlWriter getWriter() {
		return writer;
	}
	
	/** 
	 * Clean the String buffer
	 */
	public void reset() {
		writer.reset();
	}
	
	/**
	 * Reset the buffer, allocating the expected size
	 * @param expectedSize
	 */
	public void reset(int expectedSize) {
		writer.reset(expectedSize);
	}

	/**
	 * @return the contents printed to this context
	 */
	public String getContents() {
		return writer.getContents();
	}

	
}
