package org.haml4j.core;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.haml4j.model.Renderizable;

/**
 * The context used for rendering. Encapsulates the environment 
 * where a HAML template will be rendered
 * @author icoloma
 *
 */
public interface Context {

	/**
	 * @return the class usd for writing HTML
	 */
	public HtmlWriter getWriter();
	
	/**
	 * Increase the indentation level in 1. Returns the current indentation level (after addition)
	 */
	public int pushIndent();
	
	/**
	 * Decrease the indentation level in 1. Returns the current de indentation level (after substraction)
	 */
	public int popIndent();
	
	/**
	 * Prints a newline (while prettyprinting)
	 */
	public void printNewLine();
	
	/**
	 * Return true if newlines and indentation tabs should be printed
	 * @return
	 */
	public boolean isPretty();
	
	
	public ScriptEngine getScriptEngine();
	

	/**
	 * Prints a tag attribute and value, if not null
	 * @param name name of the attribute
	 * @param value value of the attribute. If null, nothing will be written
	 * @return this same instance
	 * @throws ScriptException 
	 */
	public Context printAttribute(String name, Renderizable value) throws ScriptException;

}
