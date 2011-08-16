package org.haml4j.model;

public abstract class AbstractNode implements Node {

	/** the parent of this tag, null if none */
	private Node parent;

	@Override
	public Node getParent() {
		return parent;
	}

	@Override
	public void setParent(Node parent) {
		this.parent = parent;
	}

}
