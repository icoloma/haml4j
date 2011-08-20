package org.haml4j.parser;


public interface DoctypeHandler {

	/** 
	 * Process a doctype line and produce the expected output
	 * @param doctype the doctype line, having removed the "!!!" prefix
	 * @return the doctype string
	 */
	public String processDocType(String doctype);
	
}
