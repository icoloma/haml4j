package org.haml4j.parser;

import static org.junit.Assert.assertEquals;

import org.haml4j.exception.InconsistentIndentationException;
import org.junit.Test;

public class TabIndentationStrategyTest {

	private TabIndentationStrategy strategy = new TabIndentationStrategy();
	
	@Test
	public void testGetIndent() {
		assertEquals(0, strategy.getIndentationLevel(""));
		assertEquals(2, strategy.getIndentationLevel("\t\t"));
		assertEquals(0, strategy.getIndentationLevel("a"));
		assertEquals(2, strategy.getIndentationLevel("\t\ta"));
	}
	
	@Test(expected=InconsistentIndentationException.class)
	public void testWrongIndent() {
		assertEquals(0, strategy.getIndentationLevel(" "));
	}
	
}
