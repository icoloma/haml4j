package util;

import static org.haml4j.util.SharedUtils.substring;
import static org.junit.Assert.assertEquals;

import org.haml4j.exception.UnbalancedContentsException;
import org.haml4j.util.SharedUtils;
import org.javatuples.Pair;
import org.junit.Test;

public class SharedUtilsTest {

	@Test
	public void testBalance() {
		assertBalance("Foo (Bar (Baz bang) bop)", " (Bang (bop bip))", "Foo (Bar (Baz bang) bop) (Bang (bop bip))");
	}
	
	@Test(expected=UnbalancedContentsException.class)
	public void testUnbalancedContents() {
		SharedUtils.balance("Foo (Bar ", '(', ')');
	}
	
	@Test
	public void testSubstring() throws Exception {
		assertEquals("ab", substring("abc", 0, 2));
		assertEquals("ab", substring("abc", 0, -1));
		assertEquals("abc", substring("abc", 0, 5));
		assertEquals("", substring("abc", 0, -4));
		assertEquals("", substring("abc", 5, 6));
	}

	private void assertBalance(String expectedValue, String expectedRest, String actualInput) {
		Pair<String, String> actual = SharedUtils.balance(actualInput, '(', ')');
		assertEquals(expectedValue, actual.getValue0());
		assertEquals(expectedRest, actual.getValue1());
	}
	
}
