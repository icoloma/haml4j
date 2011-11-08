package org.haml4j.model;

import javax.script.ScriptException;

import org.haml4j.core.Context;


/**
 * Parsed file contents
 * @author Nacho
 *
 */
public class Document extends AbstractNode {

	/** the file doctype */
	private String doctype;

	public String getDoctype() {
		return doctype;
	}

	public Node getRootNode() {
		return this;
	}

	public void addDoctype(String newDoctype) {
		doctype = (doctype == null? newDoctype : doctype+ newDoctype) + '\n';
	}
	
	/**
	 * Render this document to the provided context
	 * @param context
	 */
	@Override
	public void render(Context context) throws ScriptException {
		if (doctype != null) {
			context.getWriter().print(doctype);
		}
		renderChildren(context);
	}
	@Override
	protected void renderChildren(Context context) throws ScriptException {
		for (Node node : children) {
			node.render(context);
			if (context.isPretty()) {
				context.printNewLine();
			}
		}
	}
	
}
