package org.haml4j.core;

/**
 * Used to execute a HAML template that will be rendered into a String
 * @author icoloma
 *
 */
public class StringBackedContext extends AbstractContext {

	public StringBackedContext() {
		super(new StringHtmlWriter());
	}
	
	@Override
	public StringHtmlWriter getWriter() {
		return (StringHtmlWriter) super.getWriter();
	}
	
	/** 
	 * Clean the String buffer
	 */
	public void reset() {
		getWriter().reset();
	}
	
	/**
	 * Reset the buffer, allocating the expected size
	 * @param expectedSize
	 */
	public void reset(int expectedSize) {
		getWriter().reset(expectedSize);
	}

	/**
	 * @return the contents printed to this context
	 */
	public String getContents() {
		return getWriter().getContents();
	}

	
}
