package org.haml4j.model;

import org.haml4j.core.Context;
import org.haml4j.core.HtmlWriter;


/**
 * An HTML tag
 * @author Nacho
 *
 */
public class TagNode extends AbstractNode {

	/** tag name */
	private String tagName;
	
	/** tag id */
	private String id;
	
	/** tag class */
	private String cssClass;
	
	public TagNode(String tagName) {
		this.tagName = tagName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	@Override
	public void render(Context context) {
		HtmlWriter writer = context.getWriter();
		writer.open(tagName);
		writer.close();
		for (Node node : getChildren()) {
			node.render(context);
		}
		writer.close(tagName);
	}
	
}
