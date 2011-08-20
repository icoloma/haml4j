package org.haml4j.parser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.haml4j.exception.IllegalNestingException;
import org.haml4j.exception.InconsistentIndentationException;
import org.haml4j.exception.ParseException;
import org.haml4j.exception.SyntaxException;
import org.haml4j.model.CommentNode;
import org.haml4j.model.Document;
import org.haml4j.model.IfNode;
import org.haml4j.model.Node;
import org.haml4j.model.PlainNode;
import org.haml4j.model.ScriptNode;
import org.haml4j.model.TagNode;


/**
 * Parses a haml file.
 * This file should be a (more-or-less) port of parser.rb 
 * @author Indra
 *
 */
public class Parser implements ParserConstants {
	
	/** the doctype handler */
	private DoctypeHandler doctypeHandler;
	
	/** the document we are parsing */
	private Document document;
	
	/** the current node we are parsing */
	private Node currentNode;
	
	/** current level of indentation */
	private int currentIndentLevel;
	
	/**
	 * Parse a HAML document
	 */
	public Document parse(String input) {
		InputLines lines = new InputLines(input);
		try {
			document = new Document();
			currentNode = document.getRootNode();
			currentIndentLevel = -1;
			String line = lines.nextLine();
			if (lines.getCurrentIndentation() != 0) {
				throw new InconsistentIndentationException("Indenting at the beginning of the document is illegal.");
			}
			while (line != null) {
				parseLine(lines.getCurrentIndentation(), line.trim());
				line = lines.nextLine();
			}
			return document;
		} catch (ParseException e) {
			e.setLine(lines.getRow());
			throw e;
		}
	}

	/**
	 * Parse a line
	 * @param lineIndent the indentation of this line
	 * @param line the trimmed line contents
	 */
	private void parseLine(int lineIndent, String line) {
		if (line.length() == 0 || line.startsWith(SILENT_COMMENT)) {
			return;
		}
		char handle = line.charAt(0);
		if ( (handle == SCRIPT) || (handle == FLAT_SCRIPT) || (handle == SILENT_SCRIPT) ) {
			boolean printOutput = handle != SILENT_SCRIPT;
			if (!printOutput) {
				Matcher matcher = RW_IF.matcher(line);
				if (matcher.find()) {
					addNode(lineIndent, new IfNode(line.substring(matcher.end())));
					return;
				}
				matcher = RW_ELSE.matcher(line);
				if (matcher.find()) {
					Node ifNode = currentNode.getParent();
					if (!(ifNode instanceof IfNode)) {
						throw new IllegalNestingException("Matching if clause not found");
					}
					((IfNode)ifNode).startElse();
					return;
				}
			}
			addNode(lineIndent, new ScriptNode(printOutput, handle == '~', line.substring(1)));
			
		} else if (line.startsWith(DOCTYPE_HANDLE)) {
			document.addDoctype(doctypeHandler.processDocType(line.substring(3)));
		} else if (handle == COMMENT) {
			String[] parts = SharedUtils.balance(line, '[', ']');
			addNode(lineIndent, new CommentNode(parts[0], parts[1]));
		} else if (handle == DIV_CLASS) {
			addNode(lineIndent, parseDiv(line));
		} else {
			addNode(lineIndent, new PlainNode(line));
		}
		
	}
	
	private TagNode parseDiv(String line) {
		return parseTag("%div" + line);
	}
	
	private TagNode parseTag(String line) {
		Matcher matcher = Pattern.compile("%([-:\\w]+)([-:\\w\\.\\#]*)(.*)").matcher(line);
		if (!matcher.matches()) {
			throw new SyntaxException("Invalid tag: \"" + line + "\"");
		}
		String tagName = matcher.group(1);
		String attributes = matcher.group(2);
		String rest = matcher.group(3);
		if (!Pattern.matches("[\\.#](\\.|#|\\z)", attributes)) {
			throw new SyntaxException("Illegal element: classes and ids must have values.");
		}
		char c = attributes.charAt(0);
		if (c == '(') {
			/*
	          new_attributes_hash, rest, last_line = 
	          attributes_hashes[:new] = new_attributes_hash
			attributes = parse_new_attributes(rest)
			*/
		} else if (c == '[') {
	         // object_ref, rest = balance(rest, ?[, ?])
		} else {
			throw new SyntaxException("Unknown char: '" + c + "'");
		}
		if (rest.length() > 0) {
			matcher = Pattern.compile("(<>|><|[><])?([=\\/\\~&!])?(.*)?").matcher(rest);
			String nukeWhitespace = matcher.group(1);
			boolean nukeOuterWhitespace = nukeWhitespace.indexOf('>') != -1;
			boolean nukeInnerWhitespace = nukeWhitespace.indexOf('<') != -1;
		}
		return new TagNode(tagName);
	}
	
	private Map<String, String> parseNewAttributes(String line) {
		Matcher matcher = Pattern.compile("\\(\\s*").matcher(line);
		return null;
	}

	private void addNode(int newIndentLevel, Node node) {
		if (newIndentLevel > currentIndentLevel) {
			if (newIndentLevel > currentIndentLevel + 1) {
				throw new InconsistentIndentationException("The line was indented " + (newIndentLevel - currentIndentLevel) + " levels deeper than the previous line.");
			}
			currentIndentLevel++;
			currentNode.addChild(node);
		} else if (newIndentLevel == currentIndentLevel) {
			currentNode.getParent().addChild(node);
		} else {
			while (currentIndentLevel > -1) {
				currentIndentLevel--;
				currentNode = currentNode.getParent();
			}
			currentNode.getParent().addChild(node);
		}
		currentNode = node;
	}

	public void setDoctypeHandler(DoctypeHandler doctypeHandler) {
		this.doctypeHandler = doctypeHandler;
	}

}
