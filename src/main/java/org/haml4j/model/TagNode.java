package org.haml4j.model;

import java.util.Iterator;
import java.util.Map;

import javax.script.ScriptException;

import org.haml4j.core.Context;
import org.haml4j.core.HtmlWriter;

import com.google.common.base.Splitter;
import com.google.common.collect.Maps;


/**
 * An HTML tag
 * @author Nacho
 *
 */
public class TagNode extends AbstractNode {

	/** tag name */
	private String tagName;
	
	/** tag id */
	private String id;
	
	/** tag class */
	private String cssClass;
	
	/** the object_ref, if any */
	private ObjectReference objectReference;
	
	/** the attributes of this node */
	private Map<String, Text> attributes = Maps.newLinkedHashMap();
	
	/** the contents of this tag */
	private Text value;
	
	public TagNode(String tagName) {
		this.tagName = tagName;
	}

	@Override
	public void render(Context context) throws ScriptException {
		if (objectReference != null) {
			throw new UnsupportedOperationException();
		}
		HtmlWriter writer = context.getWriter();
		writer.open(tagName);
		writer.close();
		if (value != null) {
			value.render(context);
		}
		renderChildren(context);
		writer.close(tagName);
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

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public void setObjectRef(String objectRef) {
		if (objectRef == null) {
			this.objectReference = null;
		} else {
			Iterator<String> parts = Splitter.on(',').trimResults().split(objectRef).iterator();
			this.objectReference = new ObjectReference(parts.hasNext()? parts.next() : null, parts.hasNext()? parts.next() : null);
		}
	}

	/**
	 * If set, this is a reference to an Object that will provide the tag attributes in real time.
	 * See <a href="http://haml-lang.com/docs/yardoc/file.HAML_REFERENCE.html#object_reference_">object_reference</a>
	 * @author icoloma
	 *
	 */
	public static class ObjectReference {

		/** the name to search for the object in the Context */
		private String name;
		
		/** the prefix to use for the id and class attributes, if any */
		private String prefix;

		public ObjectReference(String name, String prefix) {
			this.name = name;
			this.prefix = prefix;
		}

		public String getName() {
			return name;
		}

		public String getPrefix() {
			return prefix;
		}

	}

	public Map<String, Text> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, Text> attributes) {
		this.attributes = attributes;
	}

	public void setValue(Text value) {
		this.value = value;
	}

}
