package org.haml4j.model;

public interface Node {

	public Node getParent();

	public void setParent(Node parent);

	public void addChild(Node node);

}
