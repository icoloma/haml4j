/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.haml4j.core;

import java.io.IOException;

/**
 * HTML writer 
 * 
 * @author icoloma
 */
public interface HtmlWriter {

	
	/**
	 * Print a String
	 * @param s the text to be written
	 * @return this same instance
	 */
    public HtmlWriter print(CharSequence s);
    
    /** 
     * Print a escaped String.
     * Prints the string, escaping problematic HTML characters
     */
    public HtmlWriter printAndEscape(CharSequence s);
    
    /**
     * Print ant Object, invoking its toString() method
     * @param o the object to be written
     * @return this same instance
     */
    public HtmlWriter print(Object o);

	/**
	 * Flush the output contents
	 */
	public void flush();
	
	/**
	 * Prints a closing tag like &lt;/a>
	 * @param tag
	 * @return
	 * @throws IOException 
	 */
	public HtmlWriter close(String tag);
	
	/**
	 * Starts a tag. For example, invoking open("div") will print "<div"
	 */
	public HtmlWriter open(String tagName);

	/**
	 * Closes an open tag by printing '>'
	 */
	public HtmlWriter close();
	
	/**
	 * Closes a bodyless tag. Depending on the output it may print 
	 * "/>" (XML) or ">" (HTML)
	 */
	public HtmlWriter closeEmpty();

}
