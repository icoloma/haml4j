package org.haml4j.model;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Parsed file contents
 * @author Nacho
 *
 */
public class HamlParsedFile {

	/** the file doctype */
	private String doctype;
	
	/** the lines */
	private final List<ParsedTag> tags = Lists.newArrayList();

	public String getDoctype() {
		return doctype;
	}

	public void setDoctype(String doctype) {
		this.doctype = doctype;
	}
	
	public void addTag(ParsedTag tag) {
		tags.add(tag);
	}
}
