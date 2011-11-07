package org.haml4j.model;

import org.haml4j.core.Context;

/**
 * A String that does not contain any parameters
 * @author icoloma
 *
 */
public class PlainText implements Renderizable {

	/** the string contents */
	private String contents;

	public PlainText(String contents) {
		this.contents = contents;
	}

	@Override
	public void render(Context context) {
		context.getWriter().print(contents);
	}
	
}
