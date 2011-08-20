package org.haml4j.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.haml4j.exception.UnbalancedContentsException;

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
	 */
	public static final String[] balance(String input, char start, char stop) {
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
				return new String[] { match.toString(), input.substring(matcher.end()) };
			}
		}
		throw new UnbalancedContentsException("Unbalanced contents: missing " + ( count > 0? stop : start));
	}
	
}
