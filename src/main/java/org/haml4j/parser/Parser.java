package org.haml4j.parser;

import static org.haml4j.util.SharedUtils.balance;

import java.util.List;
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
import org.haml4j.util.SharedUtils;
import org.javatuples.Pair;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


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
			Pair<String, String> parts = SharedUtils.balance(line, '[', ']');
			addNode(lineIndent, new CommentNode(parts.getValue0(), parts.getValue1()));
		} else if (handle == DIV_CLASS || handle == DIV_ID) {
			addNode(lineIndent, parseDiv(line));
		} else {
			addNode(lineIndent, new PlainNode(line));
		}
		
	}
	
	private TagNode parseDiv(String line) {
		return parseTag("%div" + line);
	}
	
	private TagNode parseTag(String line) {
		Map<String, String> attributes = null;
		String objectRef = null;
		
		Matcher matcher = Pattern.compile("%([-:\\w]+)([-:\\w\\.\\#]*)(.*)").matcher(line);
		if (!matcher.matches()) {
			throw new SyntaxException("Invalid tag: \"" + line + "\"");
		}
		String tagName = matcher.group(1);
		String sattr = matcher.group(2);
		String rest = matcher.group(3);
		if (Pattern.matches("[\\.#](\\.|#|\\z)", sattr)) {
			throw new SyntaxException("Classes and ids must have values: " + line);
		}

		while (!Strings.isNullOrEmpty(rest)) {
			char c = sattr.charAt(0);
			if (c == '(') {
				Pair<Map<String, String>, String> result = parseNewAttributes(rest);
				attributes = result.getValue0();
				rest = result.getValue1();
			} else if (c == '{') {
				Pair<Map<String, String>, String> result = parseOldAttributes(rest);
				attributes = result.getValue0();
				rest = result.getValue1();
			} else if (c == '[') {
				Pair<String, String> result = SharedUtils.balance(rest, '[', ']');
				objectRef = result.getValue0();
				rest = result.getValue1();
			} else {
				break;
			}
		}
		if (!Strings.isNullOrEmpty(rest)) {
			matcher = Pattern.compile("(<>|><|[><])?([=\\/\\~&!])?(.*)?").matcher(rest);
			String nukeWhitespace = matcher.group(1);
			boolean nukeOuterWhitespace = nukeWhitespace.indexOf('>') != -1;
			boolean nukeInnerWhitespace = nukeWhitespace.indexOf('<') != -1;
		}
		TagNode tag = new TagNode(tagName);
		tag.setAttributes(attributes);
		tag.setObjectRef(objectRef);
		return tag;
		/*
		     def tag(line)
      tag_name, attributes, attributes_hashes, object_ref, nuke_outer_whitespace,
        nuke_inner_whitespace, action, value, last_line = parse_tag(line)

      preserve_tag = @options[:preserve].include?(tag_name)
      nuke_inner_whitespace ||= preserve_tag
      preserve_tag = false if @options[:ugly]
      escape_html = (action == '&' || (action != '!' && @options[:escape_html]))

      case action
      when '/'; self_closing = true
      when '~'; parse = preserve_script = true
      when '='
        parse = true
        if value[0] == ?=
          value = unescape_interpolation(value[1..-1].strip, escape_html)
          escape_html = false
        end
      when '&', '!'
        if value[0] == ?= || value[0] == ?~
          parse = true
          preserve_script = (value[0] == ?~)
          if value[1] == ?=
            value = unescape_interpolation(value[2..-1].strip, escape_html)
            escape_html = false
          else
            value = value[1..-1].strip
          end
        elsif contains_interpolation?(value)
          value = unescape_interpolation(value, escape_html)
          parse = true
          escape_html = false
        end
      else
        if contains_interpolation?(value)
          value = unescape_interpolation(value, escape_html)
          parse = true
          escape_html = false
        end
      end

      attributes = Parser.parse_class_and_id(attributes)
      attributes_list = []

      if attributes_hashes[:new]
        static_attributes, attributes_hash = attributes_hashes[:new]
        Buffer.merge_attrs(attributes, static_attributes) if static_attributes
        attributes_list << attributes_hash
      end

      if attributes_hashes[:old]
        static_attributes = parse_static_hash(attributes_hashes[:old])
        Buffer.merge_attrs(attributes, static_attributes) if static_attributes
        attributes_list << attributes_hashes[:old] unless static_attributes || @options[:suppress_eval]
      end

      attributes_list.compact!

      raise SyntaxError.new("Illegal nesting: nesting within a self-closing tag is illegal.", @next_line.index) if block_opened? && self_closing
      raise SyntaxError.new("There's no Ruby code for #{action} to evaluate.", last_line - 1) if parse && value.empty?
      raise SyntaxError.new("Self-closing tags can't have content.", last_line - 1) if self_closing && !value.empty?

      if block_opened? && !value.empty? && !is_ruby_multiline?(value)
        raise SyntaxError.new("Illegal nesting: content can't be both given on the same line as %#{tag_name} and nested within it.", @next_line.index)
      end

      self_closing ||= !!(!block_opened? && value.empty? && @options[:autoclose].any? {|t| t === tag_name})
      value = nil if value.empty? && (block_opened? || self_closing)
      value = handle_ruby_multiline(value) if parse

      ParseNode.new(:tag, @index, :name => tag_name, :attributes => attributes,
        :attributes_hashes => attributes_list, :self_closing => self_closing,
        :nuke_inner_whitespace => nuke_inner_whitespace,
        :nuke_outer_whitespace => nuke_outer_whitespace, :object_ref => object_ref,
        :escape_html => escape_html, :preserve_tag => preserve_tag,
        :preserve_script => preserve_script, :parse => parse, :value => value)
    end
		 */
	}
	
	private Pair<Map<String, String>, String> parseNewAttributes(String line) {
		Matcher matcher = Pattern.compile("\\(\\s* ").matcher(line);
		Map<String, String> attributes = Maps.newHashMap();
		while (true) {
			Pair<String, String> nameValue = parseNewAttribute(matcher);
		}
		/*

    def parse_new_attributes(line)
      line = line.dup
      scanner = StringScanner.new(line)
      last_line = @index
      attributes = {}

      scanner.scan(/\(\s* /)
      loop do
        name, value = parse_new_attribute(scanner)
        break if name.nil?

        if name == false
          text = (Haml::Shared.balance(line, ?(, ?)) || [line]).first
          raise Haml::SyntaxError.new("Invalid attribute list: #{text.inspect}.", last_line - 1)
        end
        attributes[name] = value
        scanner.scan(/\s* /)

        if scanner.eos?
          line << " " << @next_line.text
          last_line += 1
          next_line
          scanner.scan(/\s* /)
        end
      end

      static_attributes = {}
      dynamic_attributes = "{"
      attributes.each do |name, (type, val)|
        if type == :static
          static_attributes[name] = val
        else
          dynamic_attributes << inspect_obj(name) << " => " << val << ","
        end
      end
      dynamic_attributes << "}"
      dynamic_attributes = nil if dynamic_attributes == "{}"

      return [static_attributes, dynamic_attributes], scanner.rest, last_line
    end

		 */
	}

	private Pair<String, String> parseNewAttribute(Matcher matcher) {
		if (!scan(matcher, "[-:\\w]+")) {
			scan(matcher, "\\)");
			return null;
		}
		String name = matcher.group();
		scan(matcher, "\\s*");
		if (!scan(matcher, "=")) {
			return Pair.with(name, "[:static, true]");
		}
		if (!scan(matcher, "[\"']")) {
			if (!scan(matcher, "(@@?|\\$)?\\w+")) {
				return null;
			} else {
				return Pair.with(name, "[:dynamic, " + matcher.group());
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
			return Pair.with(name, "[:static, " + content.get(0));
		}
		// TODO this is wrong, but it's still soon to know what this is expected to do 
		// return name, "[:dynamic," + '"' + content.map {|(t, v)| t == :str ? inspect_obj(v)[1...-1] : "\#{#{v}}"}.join + '"']
		String value = Joiner.on(",").join(content);
		return Pair.with(name, "[:dynamic, " + value);
	}	
	
	private Pair<Map<String, String>, String> parseOldAttributes(String line) {
		/*
		 		     def parse_old_attributes(line)
      line = line.dup
      last_line = @index

      begin
        attributes_hash, rest = balance(line, ?{, ?})
      rescue SyntaxError => e
        if line.strip[-1] == ?, && e.message == "Unbalanced brackets."
          line << "\n" << @next_line.text
          last_line += 1
          next_line
          retry
        end

        raise e
      end

      attributes_hash = attributes_hash[1...-1] if attributes_hash
      return attributes_hash, rest, last_line
    end

		 */
		Matcher matcher = Pattern.compile("\\(\\s*").matcher(line);
		throw new UnsupportedOperationException();
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
	
	/**
	 * Changes the pattern in a Matcher and scans to the next position
	 */
	private boolean scan(Matcher matcher, String regex) {
		return matcher.usePattern(Pattern.compile(regex)).find();
	}
}
