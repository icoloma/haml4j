package org.haml4j.model;

import org.haml4j.core.Context;

/**
 * A HTML node
 * @author icoloma
 *
 */
public interface Node {

	/**
	 * @return the parent of this node
	 */
	public Node getParent();

	/**
	 * Set the parent of this node
	 */
	public void setParent(Node parent);

	/**
	 * Add a child node
	 * @param node
	 */
	public void addChild(Node node);

	/**
	 * Render this node. Recursively invokes its children
	 */
	public void render(Context context);

}
