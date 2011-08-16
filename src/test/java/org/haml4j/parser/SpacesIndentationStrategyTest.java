package org.haml4j.parser;

import static org.junit.Assert.assertEquals;

import org.haml4j.exception.InconsistentIndentationException;
import org.junit.Test;

public class SpacesIndentationStrategyTest {

	private SpacesIndentationStrategy strategy = new SpacesIndentationStrategy(2);
	
	@Test
	public void testGetIndent() {
		assertEquals(0, strategy.getIndentationLevel(""));
		assertEquals(2, strategy.getIndentationLevel("    "));
		assertEquals(2, strategy.getIndentationLevel("     "));
		assertEquals(0, strategy.getIndentationLevel("a"));
		assertEquals(2, strategy.getIndentationLevel("    a"));
	}
	
	@Test(expected=InconsistentIndentationException.class)
	public void testWrongIndent() {
		assertEquals(0, strategy.getIndentationLevel(" \t"));
	}
	
}
