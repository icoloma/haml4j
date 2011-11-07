package org.haml4j.script;

import static org.junit.Assert.assertEquals;

import javax.script.ScriptEngine;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;

public class ServletBackedScriptEngineFactoryTest {

	private MockServletContext servletContext;
	
	private MockHttpServletRequest servletRequest;
	
	private ServletBackedScriptEngineFactory factory;
	
	private ScriptEngine engine;
	
	@Before
	public void setup() {
		servletContext = new MockServletContext();
		servletRequest = new MockHttpServletRequest(servletContext);
		factory = new ServletBackedScriptEngineFactory(servletContext);
		factory.setScriptEngineName("groovy");
		engine = factory.createEngine(servletRequest);
	}
	
	@Test
	public void testAttributes() throws Exception {
		servletContext.setAttribute("bar", new Dummy());
		servletRequest.setAttribute("foo", new Dummy());
		assertEquals(Integer.valueOf(10), engine.eval("foo.id + bar.id"));
	}

	static class Dummy {
		
		int id = 5;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
		
	}
	
}
