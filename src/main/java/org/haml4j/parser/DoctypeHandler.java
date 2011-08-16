package org.haml4j.parser;

import org.haml4j.model.Document;

public interface DoctypeHandler {

	/** 
	 * Process a doctype line and introduce its values into the Document instance
	 * @param doctype the doctype line, having removed the "!!!" prefix
	 * @param document the document where to inject its values
	 */
	public void processDocType(String doctype, Document document);
	
}
