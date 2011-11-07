package org.haml4j.model;

import java.util.List;

import javax.script.ScriptException;

import org.haml4j.core.Context;

import com.google.common.collect.Lists;

public abstract class AbstractNode implements Node {

	/** the parent of this tag, null if none */
	private Node parent;
	
	/** the child nodes */
	protected List<Node> children = Lists.newArrayList();
	
	@Override
	public void addChild(Node node) {
		children.add(node);
		node.setParent(this);
	}
	
	/**
	 * Renders the childre of this Node
	 */
	protected void renderChildren(Context context) throws ScriptException {
		context.pushIndent();
		for (Node node : children) {
			if (context.isPretty()) {
				context.printNewLine();
			}
			node.render(context);
		}
		context.popIndent();
		if (context.isPretty() && !children.isEmpty()) {
			context.printNewLine();
		}
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
