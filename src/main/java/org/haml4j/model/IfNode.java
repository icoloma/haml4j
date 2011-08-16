package org.haml4j.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * If-else instruction
 * @author Nacho
 *
 */
public class IfNode extends AbstractNode {

	/** the script clause to execute */
	private String clause;
	
	/** the list where processed child nodes will be appended to */
	private List<Node> activeChildren;
	
	/** the if-true nodes */
	private List<Node> ifChildren = Lists.newArrayList();
	
	/** the else nodes */
	private List<Node> elseChildren;
	
	public IfNode(String clause) {
		this.activeChildren = ifChildren;
		this.clause = clause;
	}
	
	@Override
	public void addChild(Node node) {
		activeChildren.add(node);
		node.setParent(this);
	}
	
	public void startElse() {
		this.activeChildren = elseChildren = Lists.newArrayList();
	}

}
