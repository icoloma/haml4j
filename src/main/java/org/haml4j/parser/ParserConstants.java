package org.haml4j.parser;

import java.util.regex.Pattern;

public interface ParserConstants {

    /** Designates an XHTML/XML element */
    public static final char ELEMENT = '%';

    /** Designates a `<div>` element with the given class */
    public static final char DIV_CLASS = '.';

    /** Designates a `<div>` element with the given id */
    public static final char DIV_ID = '#';

    /** Designates an XHTML/XML comment */
    public static final char COMMENT = '/';

    /** Designates an XHTML doctype or script that is never HTML-escaped */
    public static final char DOCTYPE = '!';

    /** Designates script, the result of which is output (IMPLEMENTED) */
    public static final char SCRIPT = '=';

    /** Designates script that is always HTML-escaped */
    public static final char SANITIZE = '&';

    /** Designates script, the result of which is flattened and output (IMPLEMENTED) */
    public static final char FLAT_SCRIPT = '~';

    /** Designates script which is run but not output (IMPLEMENTED) */
    public static final char SILENT_SCRIPT = '-';

    /** When following SILENT_SCRIPT, designates a comment that is not output (IMPLEMENTED) */
    public static final String SILENT_COMMENT = "-#";

    /** Designates a non-parsed line */
    public static final char ESCAPE = '\\';

    /** Designates a block of filtered text */
    public static final char FILTER = ':';

    /** Designates a non-parsed line. Not actually a character */
    public static final int PLAIN_TEXT = -1;
    
    /** The value of the character that designates that a line is part of a multiline string. */
    public static final int MULTILINE_CHAR_VALUE = '|';

    /** The starts of a doctype expression */
    public static final String DOCTYPE_HANDLE = "!!!";
    
    /** Reserved word: if */
    public static final Pattern RW_IF = Pattern.compile("-\\s*if\\s+");
    
    /** Reserved word: else */
    public static final Pattern RW_ELSE = Pattern.compile("-\\s*else\\s+");
    
    /** TBD: elif, when, case, ensure */
    
}
