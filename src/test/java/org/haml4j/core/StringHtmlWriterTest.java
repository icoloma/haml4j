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
	public void testOpenTag()  throws Exception {
		writer.open("a").close();
		writer.close("a");
		assertEquals("<a></a>", writer.getContents());
	}
	

}
