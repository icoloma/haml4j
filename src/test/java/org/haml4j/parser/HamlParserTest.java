package org.haml4j.parser;

import java.io.InputStream;

import org.junit.Before;
import org.parboiled.buffers.IndentDedentInputBuffer;
import org.parboiled.common.FileUtils;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

public class HamlParserTest {
	
	private HamlParser parser;
	
	@Before
	public void setupParser() {
		parser = Parboiled.createParser(HamlParser.class);
	}
	
	public void testParseSimple() {
		ReportingParseRunner runner = new ReportingParseRunner(parser.S());
		ParsingResult result = runner.run(
				new IndentDedentInputBuffer( read("simple.haml"), 2, "-#", false)
			);
	}
	
	private char[] read(String filename) {
		InputStream input = getClass().getResourceAsStream(filename);
		if (input == null) {
			throw new IllegalArgumentException("'" + filename + "' not found in classpath");
		}
		return FileUtils.readAllChars(input);
	}
	
}
