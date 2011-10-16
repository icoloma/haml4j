package org.haml4j.model;

import org.haml4j.core.Context;

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
	public void render(Context context) {
		context.getWriter().print(text);
		renderChildren(context);
	}
	
}
