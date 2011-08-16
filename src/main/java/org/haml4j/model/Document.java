package org.haml4j.model;


/**
 * Parsed file contents
 * @author Nacho
 *
 */
public class Document {

	/** the file doctype */
	private String doctype;
	
	/** the HTML node */
	private TagNode node;

	public Document() {
		node = new TagNode("html");
	}
	
	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}

	public TagNode getNode() {
		return node;
	}
	
}
