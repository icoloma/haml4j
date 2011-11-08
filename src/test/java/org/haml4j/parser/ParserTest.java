package org.haml4j.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

import javax.script.ScriptEngine;

import org.haml4j.core.StringBackedContext;
import org.haml4j.model.Document;
import org.haml4j.script.MapBackedScriptEngineFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class ParserTest {

	private static String[] filenames = new String[] { "templates/simple.haml" };

	private ParserFactory factory;

	private ScriptEngine scriptEngine;

	private static Logger log = LoggerFactory.getLogger(ParserTest.class);

	@Before
	public void setupParserFactory() {
		factory = new ParserFactory();
		factory.setDoctypeHandler(new XmlDoctypeHandler());
		MapBackedScriptEngineFactory factory = new MapBackedScriptEngineFactory(
				null);
		factory.setScriptEngineName("groovy");
		HashMap<Object, Object> map = Maps.newHashMap();
		map.put("item", Maps.newHashMap());
		scriptEngine = factory.createEngine(map);
	}

	@Test
	public void testParse() throws Exception {
		File folder = new File("src/test/resources/templates");

		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				log.info("Reading " + file.getAbsolutePath());

				StringBackedContext context = new StringBackedContext();
				context.setIndentChars("  ");
				context.setAttributeWrapper('\'');
				context.setScriptEngine(scriptEngine);

				String contents = readFile(file);
				Document document = factory.createParser().parse(contents);
				document.render(context);
				String expected = readFile(new File("src/test/resources/results/" + file.getName().replace("haml", "html")));
				log.info("Output:\n" + context.getContents());
				assertEquals(expected, context.getContents());
			}
		}
	}

	private String readFile(File file) throws Exception {
		return Files.toString(file, Charset.forName("utf-8"));
	}
}
