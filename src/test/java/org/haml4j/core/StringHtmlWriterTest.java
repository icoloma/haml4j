package org.haml4j.core;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class StringHtmlWriterTest {
	
	private StringHtmlWriter writer;
	
	@Before
	public void setup() {
		writer = new StringHtmlWriter();
	}
	
	@Test
	public void testPrint() throws Exception {
		writer.print("foo ");
		writer.print(4);
		assertEquals("foo 4", writer.getContents());
	}
	
	@Test
	public void testPrintAttribute() throws Exception {
		writer.attr("foo", null);
		writer.attr("bar", 4);
		assertEquals(" bar=\"4\"", writer.getContents());
		
	}
	
	@Test
	public void testStartTag()  throws Exception {
		writer.tag("a");
		writer.close("a");
		assertEquals("<a></a>", writer.getContents());
	}
	
	@Test
	public void testTagWithAttributes()  throws Exception {
		writer.tag("img", "href", "foo", "width", 5, "baz", null);
		assertEquals("<img href=\"foo\" width=\"5\">", writer.getContents());
	}
	
	@Test
	public void testBooleanTag()  throws Exception {
		writer.print("<input");
		writer.attr("required");
		writer.print(">");
		assertEquals("<input required>", writer.getContents());
	}

}
