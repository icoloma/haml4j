package org.haml4j.core;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.haml4j.model.Renderizable;

public abstract class AbstractContext implements Context {

	private ScriptEngine scriptEngine;
	
	/** true to print newlines and indentation tabs */
	private boolean pretty = true;
	
	/** the current indentation level */
	private int currentIndentLevel;
	
	/** the character to use for indenting, defaults to '\t' */
	private String indentChars = "\t";
	
	/** the character to use for attribute quotes, defaults to double quotes */
	private char attributeWrapper = '"';

	private HtmlWriter writer;

	public AbstractContext(HtmlWriter writer) {
		this.writer = writer;
	}

	@Override
	public int pushIndent() {
		return currentIndentLevel++;
	}
	
	@Override
	public int popIndent() {
		return currentIndentLevel--;
	}
	
	@Override
	public void printNewLine() {
		getWriter().print("\n");
		for (int i = 0; i < currentIndentLevel; i++) {
			getWriter().print(indentChars);
		}
	}

	@Override
	public Context printAttribute(String name, Renderizable value) throws ScriptException {
		if (value != null) {
			writer.print(" ");
			writer.print(name);
			writer.print('=');
			writer.print(attributeWrapper);
			value.render(this);
			writer.print(attributeWrapper);
		}
		return this;
	}

	@Override
	public HtmlWriter getWriter() {
		return writer;
	}

	@Override
	public boolean isPretty() {
		return pretty;
	}

	public void setPretty(boolean pretty) {
		this.pretty = pretty;
	}

	public String getIndentChars() {
		return indentChars;
	}

	public void setIndentChars(String indentChars) {
		this.indentChars = indentChars;
	}

	@Override
	public ScriptEngine getScriptEngine() {
		return scriptEngine;
	}

	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}

	public void setAttributeWrapper(char attributeWrapper) {
		this.attributeWrapper = attributeWrapper;
	}

}
