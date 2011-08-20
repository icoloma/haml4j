package org.haml4j.parser;

import org.haml4j.exception.SyntaxException;


public class Html14DoctypeHandler implements DoctypeHandler {

	@Override
	public String processDocType(String doctype) {
		doctype = doctype.trim();
		if (doctype.length() == 0) {
			return newDocType("-//W3C//DTD HTML 4.01 Transitional//EN", "http://www.w3.org/TR/html4/loose.dtd");
		} else if (doctype.equals("Strict")) {
			return newDocType("-//W3C//DTD HTML 4.01//EN", "http://www.w3.org/TR/html4/strict.dtd");
		} else if (doctype.equals("Frameset")) {
			return newDocType("-//W3C//DTD HTML 4.01 Frameset//EN", "http://www.w3.org/TR/html4/frameset.dtd");
		} else {
			throw new SyntaxException("Unrecognized doctype: " + doctype);
		}
	}

	private String newDocType(String publicName, String dtdLocation) {
		return "<!DOCTYPE html PUBLIC \"" + publicName + "\" \"" + dtdLocation + "\">";
	}

}
