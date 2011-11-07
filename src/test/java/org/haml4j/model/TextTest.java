package org.haml4j.model;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.haml4j.core.StringBackedContext;
import org.haml4j.script.MapBackedScriptEngineFactory;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class TextTest {

	private MapBackedScriptEngineFactory factory;
	
	private StringBackedContext context;
	
	@Before
	public void setupTest() {
		Map<String, Integer> map = Maps.newHashMap();
		map.put("bar", 123);
		map.put("baz", 456);
		factory = new MapBackedScriptEngineFactory(map);
		factory.setScriptEngineName("groovy");
		context = new StringBackedContext();
		context.setScriptEngine(factory.createEngine(Maps.newHashMap()));
	}
	
	@Test
	public void testParts() throws Exception {
		Text text = new Text("foo #{bar}#{baz}");
		text.render(context);
		assertEquals("foo 123456", context.getContents());
	}
	
	@Test
	public void testAllText() throws Exception {
		Text text = new Text("foo");
		text.render(context);
		assertEquals("foo", context.getContents());
	}
	
	@Test
	public void testAllVar() throws Exception {
		Text text = new Text("#{bar}");
		text.render(context);
		assertEquals("123", context.getContents());
	}
	
}
