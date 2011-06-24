package org.haml4j.parser;

import org.parboiled.Rule;

/**
 * Parses a haml file.
 * This file should be a (more-or-less) port of parser.rb 
 * @author Indra
 *
 */
@BuildParseTree
public class HamlParser extends BaseParser<Object> {

    /** Designates an XHTML/XML element */
    private static final String ELEMENT = "%";

    /** Designates a `<div>` element with the given class */
    private static final String DIV_CLASS = ".";

    /** Designates a `<div>` element with the given id */
    private static final String DIV_ID = "#";

    /** Designates an XHTML/XML comment */
    private static final String COMMENT = "/";

    /** Designates an XHTML doctype or script that is never HTML-escaped */
    private static final String DOCTYPE = "!";

    /** Designates script, the result of which is output */
    private static final String SCRIPT = "=";

    /** Designates script that is always HTML-escaped */
    private static final String SANITIZE = "&";

    /** Designates script, the result of which is flattened and output */
    private static final String FLAT_SCRIPT = "~";

    /** Designates script which is run but not output */
    private static final String SILENT_SCRIPT = "-";

    /** When following SILENT_SCRIPT, designates a comment that is not output */
    private static final String SILENT_COMMENT = "#";

    /** Designates a non-parsed line */
    private static final String ESCAPE = "\\";

    /** Designates a block of filtered text */
    private static final String FILTER = ":";

    /** Designates a non-parsed line. Not actually a character */
    private static final int PLAIN_TEXT = -1;
	
    public Rule S() {
        return Sequence();
    }

}
