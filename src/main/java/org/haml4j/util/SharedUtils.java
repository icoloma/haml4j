package org.haml4j.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.haml4j.exception.UnbalancedContentsException;
import org.javatuples.Pair;

public class SharedUtils {

	/** 
     * Moves a scanner through a balanced pair of characters.
     * For example:
     *
     *     Foo (Bar (Baz bang) bop) (Bang (bop bip))
     *     ^                       ^
     *     from                    to
     *
     * @param input The string to process
     * @param start The character opening the balanced pair.
     * @param stop The character closing the balanced pair.
     *   A `Fixnum` in 1.8, a `String` in 1.9
     * @return [(String, String)] The string matched within the balanced pair
     *   and the rest of the string.
     *   `["Foo (Bar (Baz bang) bop)", " (Bang (bop bip))"]` in the example above.
	 * @return
	 * /
	public static final Pair<String, String> balance(String input, char start, char stop) {
		int length = input.length();
		StringBuilder rest = new StringBuilder(length);
		StringBuilder match = new StringBuilder(length);
		int count = 0;
		Matcher matcher = Pattern.compile("(.*?)[\\" + start + "\\" + stop + "]").matcher(input);
		while (matcher.find()) {
			String s = matcher.group(0);
			char c = s.charAt(s.length() - 1);
			match.append(s);
			if (c == start) {
				count++;
			} else if (c == stop) {
				count--;
			}
			if (count == 0) {
				return Pair.with(match.toString(), input.substring(matcher.end()));
			}
		}
		throw new UnbalancedContentsException("Unbalanced contents: missing " + ( count > 0? stop : start));
	}*/
	public static final Pair<String, String> balance(String input, char start, char stop) {
		return balance(input, start, stop, 0);
	}
	public static final Pair<String, String> balance(String input, char start, char stop, int count) {
		Pair<String, Integer> p = balance(Pattern.compile(".").matcher(input), start, stop, 0);
		return Pair.with(p.getValue0(), input.substring(p.getValue1()));
	}
	
	
	/*
    # Moves a scanner through a balanced pair of characters.
    # For example:
    #
    #     Foo (Bar (Baz bang) bop) (Bang (bop bip))
    #     ^                       ^
    #     from                    to
    #
    # @param scanner [StringScanner] The string scanner to move
    # @param start [Character] The character opening the balanced pair.
    #   A `Fixnum` in 1.8, a `String` in 1.9
    # @param finish [Character] The character closing the balanced pair.
    #   A `Fixnum` in 1.8, a `String` in 1.9
    # @param count [Fixnum] The number of opening characters matched
    #   before calling this method
    # @return [(String, String)] The string matched within the balanced pair
    #   and the rest of the string.
    #   `["Foo (Bar (Baz bang) bop)", " (Bang (bop bip))"]` in the example above.
	 */
	// I cannot return "the rest of the String" from Java, so I just return the index of the last character matched
	public static final Pair<String, Integer> balance(Matcher matcher, char start, char stop, int count) {
		StringBuilder str = new StringBuilder();
		matcher.usePattern(Pattern.compile("(.*?)[\\" + start +"\\" + stop + "]", Pattern.MULTILINE));
		while (matcher.find()) {
			String value = matcher.group();
			str.append(value);
			count = value.charAt(value.length() -1) == start? count + 1 : count -1;
			if (count == 0) {
				return Pair.with(str.toString().trim(), matcher.end());
			}
		}
		throw new UnbalancedContentsException("Unbalanced contents: missing " + ( count > 0? stop : start));
	}
	
	/**
	 * substring, Rails style
	 * @param s the string to get a piece from
	 * @param from the starting character, inclusive
	 * @param until the end character. If less than zero, it will start from the end
	 * @return
	 */
	public static String substring(String s, int from, int until) {
		int l = s.length();
		if (from > l) {
			return "";
		}
		if (until > l) {
			until = l;
		}
		if (until < 0) {
			until = l + until;
		}
		if (until <= from) {
			return "";
		}
		return s.substring(from, until);
	}
	
}
