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
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

/**
 * A wrapper around the default writer of a {@link HttpServletResponse}.
 * @author Ignacio Coloma
 */
public class StreamHtmlWriter extends AbstractHtmlWriter {

	private PrintWriter delegate;
	
	@Override
	public StreamHtmlWriter print(CharSequence s) throws IOException {
		delegate.print(s);
		return this;
	}

	@Override
	public void flush() throws IOException {
		delegate.flush();
	}

}
