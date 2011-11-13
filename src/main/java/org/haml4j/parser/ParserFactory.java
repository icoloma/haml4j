package org.haml4j.parser;

import javax.script.ScriptEngine;

/**
 * Create a new Parser instance
 * @author icoloma
 *
 */
public class ParserFactory {

	private DoctypeHandler doctypeHandler;
	
	private ScriptEngine scriptEngine;
	
	public Parser createParser() {
		Parser parser = new Parser();
		parser.setDoctypeHandler(doctypeHandler);
		parser.setScriptEngine(scriptEngine);
		return parser;
	}

	public void setDoctypeHandler(DoctypeHandler doctypeHandler) {
		this.doctypeHandler = doctypeHandler;
	}

	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}
	
}
