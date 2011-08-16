package org.haml4j.model;

/**
 * A script piece that should be executed and included in the output
 * @author icoloma
 *
 */
public class ScriptNode extends AbstractNode {

	/** the script contents */
	private String contents;

	/** true to print output, false to only run contents */
	private boolean printOutput;
	
	/** true to flatten output */
	private boolean flattenOutput;
	
	public ScriptNode(boolean printOutput, boolean flattenOutput, String contents) {
		this.printOutput = printOutput;
		this.flattenOutput = flattenOutput;
		this.contents = contents;
	}

	public String getContents() {
		return contents;
	}

	public boolean isPrintOutput() {
		return printOutput;
	}

	public boolean isFlattenOutput() {
		return flattenOutput;
	}

	@Override
	public void addChild(Node node) {
		throw new UnsupportedOperationException("Script nodes should not have any children");
	}

}
