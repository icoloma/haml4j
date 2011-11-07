package org.haml4j.script;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import javax.script.ScriptEngine;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class MapBackedScriptEngineFactoryTest {

	private Map<String, Object> applicationContext;
	
	private Map<String, Object> requestContext;
	
	private MapBackedScriptEngineFactory factory;
	
	private ScriptEngine engine;
	
	@Before
	public void setup() {
		applicationContext = Maps.newHashMap();
		requestContext = Maps.newHashMap();
		factory = new MapBackedScriptEngineFactory(applicationContext);
		factory.setScriptEngineName("groovy");
		engine = factory.createEngine(requestContext);
	}
	
	@Test
	public void testAttributes() throws Exception {
		applicationContext.put("bar", new Dummy());
		requestContext.put("foo", new Dummy());
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
