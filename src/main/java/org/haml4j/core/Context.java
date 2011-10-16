package org.haml4j.core;

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
	 * Execute a scriptlet. Depending on the underlying engine, 
	 * the scriptlet can be executed by a groovy engine, mvel, JSP EL, etc. 
	 * @param scriptlet the scriptlet to execute
	 * @return the result of executing the piece of code provided
	 */
	public Object execute(String scriptlet);
	
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
	
}
