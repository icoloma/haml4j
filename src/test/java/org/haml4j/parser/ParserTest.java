package org.haml4j.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;

import org.haml4j.core.StringBackedContext;
import org.haml4j.model.Document;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteStreams;

public class ParserTest {

	private static String[] filenames = new String[] {
		"templates/simple.haml"
	};
	
	private ParserFactory factory;
	
	private static Logger log = LoggerFactory.getLogger(ParserTest.class);
	
	@Before
	public void setupParserFactory() {
		factory = new ParserFactory();
		factory.setDoctypeHandler(new XmlDoctypeHandler());
	}
	
	@Test
	public void testParse() throws Exception {
	  for (String filename: filenames) {
		  log.info("Reading " + filename);
		  String contents = readFile(filename);
		  Document document = factory.createParser().parse(contents);
		  StringBackedContext context = new StringBackedContext();
		  context.setIndentChars("  ");
		  document.render(context);
		  String expected = readFile(filename.replace("haml", "html").replace("templates", "results"));
		  log.info("Output:\n" + context.getContents());
		  assertEquals(expected, context.getContents());
	  }
	}

	private String readFile(String filename) throws Exception {
		InputStream input = getClass().getClassLoader().getSystemResourceAsStream(filename);
		  assertNotNull(input);
		  String contents = new String(ByteStreams.toByteArray(input), "utf-8");
		return contents;
	}
}
