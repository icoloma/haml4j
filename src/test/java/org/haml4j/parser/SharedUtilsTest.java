package org.haml4j.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SharedUtilsTest {

	@Test
	public void testBalance() {
		String[] expected = new String[] { "Foo (Bar (Baz bang) bop)", " (Bang (bop bip))" };
		String[] actual = SharedUtils.balance("Foo (Bar (Baz bang) bop) (Bang (bop bip))", '(', ')');
		assertEquals(expected[0], actual[0]);
		assertEquals(expected[1], actual[1]);
	}
	
}
