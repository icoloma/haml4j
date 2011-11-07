package org.haml4j.model;

import java.util.List;

import javax.script.ScriptException;

import org.haml4j.core.Context;

import com.google.common.collect.Lists;

/**
 * If-else instruction
 * @author Nacho
 *
 */
public class IfNode extends AbstractNode {

	/** the script clause to execute */
	private String clause;
	
	/** the list of nodes that will be used if clause if true */
	private List<Node> ifChildren;
	
	/** the else nodes */
	private List<Node> elseChildren;
	
	public IfNode(String clause) {
		this.ifChildren = getChildren();
		this.clause = clause;
	}
	
	public void startElse() {
		this.elseChildren = Lists.newArrayList();
		this.children = elseChildren;
	}
	
	@Override
	public void render(Context context) throws ScriptException {
		Boolean clauseValue = (Boolean) context.getScriptEngine().eval(clause);
		this.children = clauseValue? ifChildren : elseChildren;
		this.renderChildren(context);
	}

}
