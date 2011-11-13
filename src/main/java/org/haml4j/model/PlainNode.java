package org.haml4j.model;

import javax.script.ScriptException;

import org.haml4j.core.Context;
import org.haml4j.exception.ParseException;

/**
 * Plain text not interpreted by the parser
 * @author icoloma
 *
 */
public class PlainNode extends AbstractNode {

	private String text;

	public PlainNode(String text) {
		this.text = text;
	}
	
	@Override
	public void render(Context context) throws ScriptException {
		context.getWriter().print(text);
		renderChildren(context);
	}
	
	@Override
	public void addChild(Node node) {
		throw new ParseException("Illegal nesting: nesting within plain text is illegal.");
	}
	
}
