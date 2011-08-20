package org.haml4j.model;

import java.util.List;

import com.google.common.collect.Lists;

public abstract class AbstractNode implements Node {

	/** the parent of this tag, null if none */
	private Node parent;
	
	/** the child nodes */
	private List<Node> children = Lists.newArrayList();
	
	@Override
	public void addChild(Node node) {
		children.add(node);
		node.setParent(this);
	}

	public List<Node> getChildren() {
		return children;
	}

	@Override
	public Node getParent() {
		return parent;
	}

	@Override
	public void setParent(Node parent) {
		this.parent = parent;
	}

}
