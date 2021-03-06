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


/**
 * Abstract writer implementation
 * @author icoloma
 */
public abstract class AbstractHtmlWriter implements HtmlWriter {

	private static String[] HTML_ESCAPE;
	
	static {
		HTML_ESCAPE = new String[63];
		HTML_ESCAPE['&'] = "&amp;";
		HTML_ESCAPE['<'] = "&lt;";
		HTML_ESCAPE['>'] = "&gt;";
		HTML_ESCAPE['"'] = "&quot;";
		HTML_ESCAPE['\''] = "&#039;";
		HTML_ESCAPE['\n'] = "&#x000A;";
	}
	
	@Override
	public HtmlWriter printAndEscape(CharSequence s) {
		StringBuilder b = new StringBuilder(s.length());
		int l = s.length();
		for (int i = 0; i < l; i++) {
			char c = s.charAt(i);
			if (c > HTML_ESCAPE.length || HTML_ESCAPE[c] == null) {
				b.append(c);
			} else {
				b.append(HTML_ESCAPE[c]);
			}
		}
		return this.print(b);
	}
	
	@Override
	public HtmlWriter open(String tagName) {
		return print('<').print(tagName);
	}
	
	@Override
	public HtmlWriter close() {
		return print('>');
	}
	
	@Override
	public HtmlWriter closeEmpty() {
		return print('>');
	}

	@Override
	public HtmlWriter print(Object o) {
		return print(o == null? "null" : o.toString());
	}
/*
	@Override
	public HtmlWriter tag(String tag, Object... attributes) {
		open(tag);
		if (attributes.length % 2 > 0) {
			throw new IllegalArgumentException("The number of attribute names and values should be even: " + Joiner.on(", ").join(attributes));
		}
		int i = 0;
		while (i < attributes.length) {
			attr((String)attributes[i], attributes[i + 1]);
			i += 2;
		}
		return close();
	}
*/
	@Override
	public HtmlWriter close(String tag) {
		return print("</").print(tag).print(">");
	}
}
