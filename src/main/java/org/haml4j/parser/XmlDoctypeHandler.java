package org.haml4j.parser;

import java.util.Iterator;

import org.haml4j.exception.ParseException;

import com.google.common.base.Splitter;

public class XmlDoctypeHandler implements DoctypeHandler {

	@Override
	public String processDocType(String doctype) {
		Iterator<String> parts = Splitter.on(' ').omitEmptyStrings().split(doctype).iterator();
		if (!parts.hasNext()) {
			return newDocType("-//W3C//DTD XHTML 1.0 Transitional//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
		}
		String head = parts.next();
		if (head.equals("XML")) {
			return "<?xml version=\"1.0\" encoding=\"" + (parts.hasNext()? parts.next().toLowerCase() : "utf-8") + "\"' ?>";
		} else if (head.equals("Strict")) {
			return newDocType("-//W3C//DTD XHTML 1.0 Strict//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
		} else if (head.equals("Frameset")) {
			return newDocType("-//W3C//DTD XHTML 1.0 Frameset//EN", "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
		} else if (head.equals("1.1")) {
			return newDocType("-//W3C//DTD XHTML 1.1//EN", "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd");
		} else if (head.equals("Basic")) {
			return newDocType("-//W3C//DTD XHTML Basic 1.1//EN", "http://www.w3.org/TR/xhtml-basic/xhtml-basic11.dtd");
		} else if (head.equals("Mobile")) {
			return newDocType("-//WAPFORUM//DTD XHTML Mobile 1.2//EN", "http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd");
		} else if (head.equals("RDFa")) {
			return newDocType("-//W3C//DTD XHTML+RDFa 1.0//EN", "http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd");
		} else if (head.equals("5")) {
			return "<!DOCTYPE html>";
		} else {
			throw new ParseException("Unrecognized doctype: " + head);
		}
	}

	private String newDocType(String publicName, String dtdLocation) {
		return "<!DOCTYPE html PUBLIC \"" + publicName + "\" \"" + dtdLocation + "\">";
	}

}
