package org.haml4j.model;

import org.haml4j.core.Context;


/**
 * Parsed file contents
 * @author Nacho
 *
 */
public class Document {

	/** the file doctype */
	private String doctype;
	
	/** the HTML node */
	private Node node;

	public Document() {
		node = new TagNode("html");
	}
	
	public String getDoctype() {
		return doctype;
	}

	public Node getRootNode() {
		return node;
	}

	public void addDoctype(String newDoctype) {
		doctype = doctype == null? newDoctype : doctype + newDoctype;
	}
	
	/**
	 * Render this document to the provided context
	 * @param context
	 */
	public void render(Context context) {
		context.getWriter().print(doctype);
		node.render(context);
	}
	
}
