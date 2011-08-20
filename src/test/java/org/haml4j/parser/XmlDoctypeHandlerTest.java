package org.haml4j.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class XmlDoctypeHandlerTest {

	private XmlDoctypeHandler handler = new XmlDoctypeHandler();
	
	@Test
	public void test() {

		assertXmlStart("!!! XML", "utf-8");
		assertXmlStart("!!! XML ISO-8859-1", "iso-8859-1");
		assertXmlStart("!!! XML UtF-8 Foo bar", "utf-8");
		
		assertEquals("<!DOCTYPE html>", handler.processDocType("!!! 5".substring(3)));
		assertDoctype("!!!", "-//W3C//DTD XHTML 1.0 Transitional//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
		assertDoctype("!!! Strict", "-//W3C//DTD XHTML 1.0 Strict//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
		assertDoctype("!!! Frameset", "-//W3C//DTD XHTML 1.0 Frameset//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
		assertDoctype("!!! 1.1", "-//W3C//DTD XHTML 1.1//EN", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd");
		assertDoctype("!!! Basic", "-//W3C//DTD XHTML Basic 1.1//EN", "http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd");
		assertDoctype("!!! Mobile", "-//WAPFORUM//DTD XHTML Mobile 1.2//EN", "http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd");
		assertDoctype("!!! RDFa", "-//W3C//DTD XHTML+RDFa 1.0//EN", "http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd");

	}

	private void assertXmlStart(String hamlDoctype, String expectedEncoding) {
		String actualDoctype = handler.processDocType(hamlDoctype.substring(3));
		assertEquals("<?xml version=\"1.0\" encoding=\"" + expectedEncoding + "\"' ?>", actualDoctype);
	}

	private void assertDoctype(String hamlDoctype, String publicName, String dtdLocation) {
		String actualDoctype = handler.processDocType(hamlDoctype.substring(3));
		assertEquals("<!DOCTYPE html PUBLIC \"" + publicName + "\" \"" + dtdLocation + "\">", actualDoctype);
	}
	
}
