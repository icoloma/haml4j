package org.haml4j.model;

/**
 * An HTML tag
 * @author Nacho
 *
 */
public class ParsedTag {

	/** tag name */
	private String name;
	
	/** tag id */
	private String id;
	
	/** tag class */
	private String cssClass;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}
	
}
