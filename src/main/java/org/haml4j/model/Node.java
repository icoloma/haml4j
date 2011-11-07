package org.haml4j.model;


/**
 * A HTML node
 * @author icoloma
 *
 */
public interface Node extends Renderizable {

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


}
