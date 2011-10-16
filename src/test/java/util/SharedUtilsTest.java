package util;

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

	private void assertBalance(String expectedValue, String expectedRest, String actualInput) {
		Pair<String, String> actual = SharedUtils.balance(actualInput, '(', ')');
		assertEquals(expectedValue, actual.getValue0());
		assertEquals(expectedRest, actual.getValue1());
	}
	
}
