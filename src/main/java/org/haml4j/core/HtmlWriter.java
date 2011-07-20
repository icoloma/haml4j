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
	 * Prints a tag attribute and value, if not null
	 * @param name name of the attribute
	 * @param value value of the attribute. If null, nothing will be written
	 * @return this same instance
	 */
	public HtmlWriter attr(String name, Object value) throws IOException;

	/**
	 * Prints a tag boolean attribute. These attributes just have to be present, with no actual value
	 * @param name name of the attribute
	 * @return this same instance
	 */
	public HtmlWriter attr(String name) throws IOException;
	
	/**
	 * Print a String
	 * @param s the text to be written
	 * @return this same instance
	 */
    public HtmlWriter print(CharSequence s) throws IOException;
    
    /**
     * Print ant Object, invoking its toString() method
     * @param o the object to be written
     * @return this same instance
     */
    public HtmlWriter print(Object o) throws IOException;

	/**
	 * Flush the output contents
	 */
	public void flush() throws IOException;
	
	/**
	 * Start an HTML tag.
	 * This method will write the starting tag complete with all the provided attributes.
	 * The attributes must be in pairs (name, value)
	 * Example of use: start("a", "href", "foo.html", "class", "bar") will write 
	 * &lt;a href="foo.html" class="bar">
	 * If any of the provided attribute values is null, it will be ignored. 
	 * @throws IllegalArgumentException if the number of attribute names and values do 
	 * not match.  
	 */
	public HtmlWriter tag(String tag, Object... attributes) throws IOException;

	/**
	 * Prints a closing tag like &lt;/a>
	 * @param tag
	 * @return
	 * @throws IOException 
	 */
	public HtmlWriter close(String tag) throws IOException;
	
}
