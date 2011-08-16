package org.haml4j.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class InputLinesTest {

	@Test
	public void testSingleLine() {
		InputLines lines = new InputLines("a\n\nb\nc");
		assertEquals("a", lines.nextLine());
		assertEquals(2, lines.getRow());
		assertEquals("", lines.nextLine());
		assertEquals("b", lines.nextLine());
		assertEquals("c", lines.nextLine());
		assertNull(lines.nextLine());
	}
	
	@Test
	public void testMultipleLine() {
		InputLines lines = new InputLines("foo  |\nbar|\nbaz");
		assertEquals("foo\nbar\n", lines.nextLine());
		assertEquals(3, lines.getRow());
		assertEquals("baz", lines.nextLine());
		assertEquals(4, lines.getRow());
		assertNull(lines.nextLine());
	}
	
	@Test
	public void testIndent() {
		InputLines lines = new InputLines("foo\n    bar");
		lines.nextLine();
		assertEquals(0, lines.getCurrentIndentation());
		lines.nextLine();
		assertEquals(2, lines.getCurrentIndentation());
	}
	
}
