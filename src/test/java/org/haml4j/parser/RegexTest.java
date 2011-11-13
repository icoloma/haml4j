package org.haml4j.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class RegexTest implements ParserConstants {

	private Pattern pattern;
	
	@Test
	public void testAttributes() throws Exception {
		pattern = LITERAL_VALUE_REGEX;
		assertValid(":foo");
		assertValid("'boom=>biddly'");
		assertValid("'foo,bar'");
		
	}
	
	@Test
	public void testAttributeExpression() {
		assertAttributeExpression("'boom=>biddly' => 'bar => baz'", "'boom=>biddly'", "'bar => baz'");
	}
	
	private void assertAttributeExpression(String expression, String key, String value) {
		StringScanner scanner = new StringScanner(expression);
		scanner.scan(SPACES);
		String actualKey = scanner.scan(ParserConstants.LITERAL_VALUE_REGEX);
		assertEquals(key, actualKey);
		assertNotNull(scanner.scan(MAP_ATTRIBUTE_SEPARATOR));
		assertEquals(value, scanner.scan(LITERAL_VALUE_REGEX));
	}
	
	private void assertValid(String s) {
		Matcher matcher = pattern.matcher(s);
		assertTrue(matcher.find());
		assertEquals(matcher.group(), s);
	}
	private boolean scan(Matcher matcher, Pattern regex) {
		return matcher.usePattern(regex).find();
	}
}
