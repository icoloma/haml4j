package org.haml4j.model;

import java.util.List;

import com.google.common.collect.Lists;

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
	
	/** the child nodes */
	private List<Node> children = Lists.newArrayList();
	
	public TagNode(String tagName) {
		this.tagName = tagName;
	}
	
	public void addChild(Node node) {
		children.add(node);
		node.setParent(this);
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

	public List<Node> getChildren() {
		return children;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
}
