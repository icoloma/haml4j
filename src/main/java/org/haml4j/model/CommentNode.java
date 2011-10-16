package org.haml4j.model;

import org.haml4j.core.Context;
import org.haml4j.core.HtmlWriter;
import org.haml4j.exception.IllegalNestingException;

import com.google.common.base.Strings;

/**
 * A comment that will be rendered as HTML comment
 * @author icoloma
 *
 */
public class CommentNode extends AbstractNode {

	/** the IE conditional comment */
	private String ieComment;
	
	/** the inline content */
	private String contents;

	public CommentNode(String ieComment, String contents) {
		this.ieComment = ieComment;
		this.contents = contents;
	}

	@Override
	public void addChild(Node node) {
		if (!Strings.isNullOrEmpty(contents)) {
			throw new IllegalNestingException("Illegal nesting: nesting within a tag that already has content is illegal.");
		}
		super.addChild(node);
	}
	
	@Override
	public void render(Context context) {
		HtmlWriter writer = context.getWriter();
		writer.print("<!--");
		renderChildren(context);
		writer.print("-->");
	}
	
}
