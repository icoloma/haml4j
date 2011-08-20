package org.haml4j.model;

/**
 * Plain text not interpreted by the parser
 * @author icoloma
 *
 */
public class PlainNode extends AbstractNode {

	private String text;

	public PlainNode(String text) {
		this.text = text;
	}
	
}
