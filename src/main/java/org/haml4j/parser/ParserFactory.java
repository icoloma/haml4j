package org.haml4j.parser;

/**
 * Create a new Parser instance
 * @author icoloma
 *
 */
public class ParserFactory {

	private DoctypeHandler doctypeHandler;
	
	public Parser createParser() {
		Parser parser = new Parser();
		parser.setDoctypeHandler(doctypeHandler);
		return parser;
	}

	public void setDoctypeHandler(DoctypeHandler doctypeHandler) {
		this.doctypeHandler = doctypeHandler;
	}
	
}
