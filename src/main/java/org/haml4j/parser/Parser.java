package org.haml4j.parser;

import static org.haml4j.util.SharedUtils.balance;
import static org.haml4j.util.SharedUtils.substring;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.haml4j.exception.IllegalNestingException;
import org.haml4j.exception.InconsistentIndentationException;
import org.haml4j.exception.ParseException;
import org.haml4j.exception.UnbalancedContentsException;
import org.haml4j.model.CommentNode;
import org.haml4j.model.Document;
import org.haml4j.model.IfNode;
import org.haml4j.model.Node;
import org.haml4j.model.PlainNode;
import org.haml4j.model.ScriptNode;
import org.haml4j.model.TagNode;
import org.haml4j.model.Text;
import org.haml4j.util.SharedUtils;
import org.javatuples.Pair;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * Parses a haml file.
 * This file should be a (more-or-less) port of parser.rb 
 * This class is NOT thread-safe
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
	
	/** the file contents */
	private InputLines lines;
	
	/* the current line */
	private String line;
	
	/** the last indentation level */
	private int lastIndentLevel;
	
	private ScriptEngine scriptEngine;
	
	/**
	 * Parse a HAML document
	 */
	public Document parse(String input) {
		lines = new InputLines(input);
		try {
			document = new Document();
			currentNode = document.getRootNode();
			lastIndentLevel = -1;
			line = lines.nextLine();
			if (lines.getCurrentIndentation() != 0) {
				throw new InconsistentIndentationException("Indenting at the beginning of the document is illegal.");
			}
			while (line != null) {
				processLine();
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
	private void processLine() {
		if (line.length() == 0 || line.startsWith(SILENT_COMMENT)) {
			return;
		}
		char handle = line.charAt(0);
		
		if (handle == DIV_CLASS || handle == DIV_ID) {
			if (line.length() > 1 && line.charAt(1) == '{') {
				addNode(new PlainNode(line));
			} else {
				addNode(parseDiv(line));
			}
		} else if (handle == ELEMENT) {
			addNode(parseTag(line));
		} else if (handle == COMMENT) {
			Pair<String, String> parts = SharedUtils.balance(substring(line, 1, -1), '[', ']');
			addNode(new CommentNode(parts.getValue0(), parts.getValue1()));
		} else if (handle == SANITIZE) {
			char c = line.charAt(1);
			if (c == '=' && line.charAt(2) == '=') {
				addNode(new PlainNode(substring(line, 3, - 1)));
			} else if (c == SCRIPT) {
				addNode(new ScriptNode(true, false, substring(line, 2, -1)));
			} else if (c == FLAT_SCRIPT) {
				throw new UnsupportedOperationException();
			} else {
				addNode(new PlainNode(line));
			}
		} else if ( (handle == SCRIPT) || (handle == FLAT_SCRIPT) || (handle == SILENT_SCRIPT) ) {
			boolean printOutput = handle != SILENT_SCRIPT;
			if (!printOutput) {
				Matcher matcher = RW_IF.matcher(line);
				if (matcher.find()) {
					addNode(new IfNode(substring(line, 0, matcher.end())));
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
			addNode(new ScriptNode(printOutput, handle == '~', line.substring(1)));
				
		} else if (line.startsWith(DOCTYPE_HANDLE)) {
			document.addDoctype(doctypeHandler.processDocType(line.substring(3)));
			/*
      when ESCAPE; push plain(text[1..-1])
			*/
		} else {
			addNode(new PlainNode(line));
		}
		
	}
	
	private TagNode parseDiv(String line) {
		return parseTag("%div" + line);
	}
	
	private void parseClassAndId(Map<String, Text> attributes, String sattr) {
		if (Pattern.matches("[\\.#](\\.|#|\\z)", sattr)) {
			throw new ParseException("Classes and ids must have values: " + sattr);
		}
		String className = null;
		Matcher matcher = Pattern.compile("([#.])([-:_a-zA-Z0-9]+)").matcher(sattr);
		while (matcher.find()) {
			char handle = matcher.group(0).charAt(0);
			if (handle == '.') {
				className = className == null? matcher.group(2) : className + ' ' + matcher.group(2);						 
			} else if (handle == '#') {
				attributes.put("id", new Text(matcher.group(2)));
			} else {
				throw new IllegalStateException("We cannot get here! Don't know how to handle: " + matcher.group());
			}
		}
		if (!matcher.hitEnd()) {
			throw new ParseException("Wrong id/class syntax: " + sattr);
		}
		if (className != null) {
			attributes.put("class", new Text(className));
		}
	}
	
	private TagNode parseTag(String line) {
		Map<String, Text> attributes = Maps.newLinkedHashMap();
		String objectRef = null;
		
		Matcher matcher = Pattern.compile("%([-:\\w]+)([-:\\w\\.\\#]*)(.*)").matcher(line);
		if (!matcher.matches()) {
			throw new ParseException("Invalid tag: \"" + line + "\"");
		}
		String tagName = matcher.group(1);
		String sattr = matcher.group(2);
		String rest = matcher.group(3);
		parseClassAndId(attributes, sattr);

		while (!Strings.isNullOrEmpty(rest)) {
			char c = rest.charAt(0);
			if (c == '(') {
				Pair<Map<String, Text>, String> result = parseNewAttributes(rest);
				//attributes = result.getValue0();
				rest = result.getValue1();
			} else if (c == '{') {
				rest = parseOldAttributes(attributes, rest);
			} else if (c == '[') {
				Pair<String, String> result = SharedUtils.balance(rest, '[', ']');
				objectRef = result.getValue0();
				rest = result.getValue1();
			} else {
				break;
			}
		}
		TagNode tag = new TagNode(tagName);
		tag.setAttributes(attributes);
		tag.setObjectRef(objectRef);
		if (!Strings.isNullOrEmpty(rest)) {
			matcher = Pattern.compile("(<>|><|[><])?([=\\/\\~&!])?(.*)?").matcher(rest);
			matcher.find();
			String nukeWhitespace = matcher.group(1);
			boolean nukeOuterWhitespace = nukeWhitespace != null && nukeWhitespace.indexOf('>') != -1;
			boolean nukeInnerWhitespace = nukeWhitespace != null && nukeWhitespace.indexOf('<') != -1;
			rest = matcher.group(3);
			if (!Strings.isNullOrEmpty(rest)) {
				rest = rest.trim();
				tag.setValue("=".equals(matcher.group(2))? new Text(rest, true) : new Text(rest));
			}
		}
		return tag;
		
	}
	
	private Pair<Map<String, Text>, String> parseNewAttributes(String line) {
		Matcher matcher = Pattern.compile("\\(\\s* ").matcher(line);
		Map<String, Text> attributes = Maps.newHashMap();
		while (true) {
			Pair<String, Text> nameValue = parseNewAttribute(matcher);
		}
	}

	private Pair<String, Text> parseNewAttribute(Matcher matcher) {
		if (!scan(matcher, "[-:\\w]+")) {
			scan(matcher, "\\)");
			return null;
		}
		String name = matcher.group();
		scan(matcher, "\\s*");
		if (!scan(matcher, "=")) {
			return Pair.with(name, new Text(matcher.group()));
		}
		if (!scan(matcher, "[\"']")) {
			if (!scan(matcher, "(@@?|\\$)?\\w+")) {
				return null;
			} else {
				return Pair.with(name, new Text(matcher.group()));
			}
		}
		String quote = matcher.group();
		Pattern re = Pattern.compile("((?:\\\\.|\\#(?!\\{)|[^" + quote + "\\])*)(" + quote + "|#\\{)");
		List<String> content = Lists.newArrayList();
		while (true) {
			if (!matcher.usePattern(re).find()) {
				return null;
			}
			String value = matcher.group(1).replaceAll("\\\\(.)", "$1");
			content.add("[:str, " + value);
			if (matcher.group(2).equals(quote)) {
				break;
			}
			value = balance(matcher, '{', '}', 1).getValue0();
			content.add("[:ruby, " + value);
		}
		if (content.size() == 1) {
			return Pair.with(name, new Text(content.get(0)));
		}
		// TODO this is wrong, but it's still soon to know what this is expected to do 
		// return name, "[:dynamic," + '"' + content.map {|(t, v)| t == :str ? inspect_obj(v)[1...-1] : "\#{#{v}}"}.join + '"']
		String value = Joiner.on(",").join(content);
		return Pair.with(name, new Text(value));
	}	
	
	// parse_old_attributes
	private String parseOldAttributes(Map<String, Text> attributes, String line) {
		try {
			while (true) {
				try {
					Pair<String, String> parsed = SharedUtils.balance(line, '{', '}');
					String attributesHash = substring(parsed.getValue0(), 1, -1);

					// parse_static_hash
					StringScanner scanner = new StringScanner(attributesHash);
					scanner.scan(SPACES);
					while (!scanner.eos()) {
						String key = scanner.scan(LITERAL_VALUE_REGEX);
						String value = null;
						if (key != null) {
							if (scanner.scan(MAP_ATTRIBUTE_SEPARATOR) != null) {
								value = scanner.scan(LITERAL_VALUE_REGEX);
								scanner.scan(Pattern.compile("\\s*(?:,|$)\\s*"));
							}
							
						}
						if (key == null || value == null) {
							throw new ParseException("Malformed attributes: " + line);
						}
						
						// attributes[eval(key).to_s] = eval(value).to_s
						attributes.put(
								key.startsWith(":")? key.substring(1) : scriptEngine.eval(key).toString(), 
								new Text(scriptEngine.eval(value).toString()));
					}
					
					return parsed.getValue1();
				} catch (UnbalancedContentsException e) {
					line += '\n' + lines.nextLine();
				}
			}
		} catch (ScriptException e) {
			throw new ParseException(e.getMessage());
		}
	}

	private void addNode(Node node) {
		int newIndentLevel = lines.getCurrentIndentation();
		if (newIndentLevel > lastIndentLevel) {
			if (newIndentLevel > lastIndentLevel + 1) {
				throw new InconsistentIndentationException("The line was indented " + (newIndentLevel - lastIndentLevel) + " levels deeper than the previous line.");
			}
			lastIndentLevel++;
			currentNode.addChild(node);
		} else if (newIndentLevel == lastIndentLevel) {
			currentNode.getParent().addChild(node);
		} else {
			while (lastIndentLevel > 0) {
				lastIndentLevel--;
				currentNode = currentNode.getParent();
			}
			currentNode.getParent().addChild(node);
		}
		currentNode = node;
	}

	public void setDoctypeHandler(DoctypeHandler doctypeHandler) {
		this.doctypeHandler = doctypeHandler;
	}
	
	/**
	 * Changes the pattern in a Matcher and scans to the next position
	 */
	private boolean scan(Matcher matcher, String regex) {
		return scan(matcher, Pattern.compile(regex));
	}
	private boolean scan(Matcher matcher, Pattern regex) {
		return matcher.usePattern(regex).find();
	}

	public void setScriptEngine(ScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}
}
