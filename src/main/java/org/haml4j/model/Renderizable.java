package org.haml4j.model;

import javax.script.ScriptException;

import org.haml4j.core.Context;

public interface Renderizable {

	/**
	 * Render this instance.
	 */
	public void render(Context context) throws ScriptException;
	
}
