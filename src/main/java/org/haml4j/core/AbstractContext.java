package org.haml4j.core;

import org.haml4j.el.ExpressionLanguageEngine;

public abstract class AbstractContext implements Context {

	private ExpressionLanguageEngine expressionLanguageEngine;
	
	@Override
	public Object execute(String scriptlet) {
		return expressionLanguageEngine.execute(scriptlet);
	}

	public void setExpressionLanguageEngine( ExpressionLanguageEngine expressionLanguageEngine) {
		this.expressionLanguageEngine = expressionLanguageEngine;
	}
	
}
