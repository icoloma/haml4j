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
	
}
