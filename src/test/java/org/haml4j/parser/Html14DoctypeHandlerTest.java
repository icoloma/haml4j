package org.haml4j.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Html14DoctypeHandlerTest {

	private Html14DoctypeHandler handler = new Html14DoctypeHandler();
	
	@Test
	public void test() {
		assertDoctype("!!!", "-//W3C//DTD HTML 4.01 Transitional//EN", "http://www.w3.org/TR/html4/loose.dtd");
		assertDoctype("!!! Strict", "-//W3C//DTD HTML 4.01//EN", "http://www.w3.org/TR/html4/strict.dtd");
		assertDoctype("!!! Frameset", "-//W3C//DTD HTML 4.01 Frameset//EN", "http://www.w3.org/TR/html4/frameset.dtd");
	}

	private void assertDoctype(String hamlDoctype, String publicName, String dtdLocation) {
		String actualDoctype = handler.processDocType(hamlDoctype.substring(3));
		assertEquals("<!DOCTYPE html PUBLIC \"" + publicName + "\" \"" + dtdLocation + "\">", actualDoctype);
	}
	
}
