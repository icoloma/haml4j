package org.haml4j.parser;

public class Html5DoctypeHandler implements DoctypeHandler {

	@Override
	public String processDocType(String doctype) {
		return "<!DOCTYPE html>";
	}

}
