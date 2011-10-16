package org.haml4j.core;

import org.haml4j.el.ExpressionLanguageEngine;

public abstract class AbstractContext implements Context {

	private ExpressionLanguageEngine expressionLanguageEngine;
	
	/** true to print newlines and indentation tabs */
	private boolean pretty = true;
	
	/** the current indentation level */
	private int currentIndentLevel;
	
	/** the character to use for indenting, defaults to '\t' */
	private String indentChars = "\t";
	
	private HtmlWriter writer;

	public AbstractContext(HtmlWriter writer) {
		this.writer = writer;
	}

	@Override
	public Object execute(String scriptlet) {
		return expressionLanguageEngine.execute(scriptlet);
	}

	public void setExpressionLanguageEngine( ExpressionLanguageEngine expressionLanguageEngine) {
		this.expressionLanguageEngine = expressionLanguageEngine;
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

}
