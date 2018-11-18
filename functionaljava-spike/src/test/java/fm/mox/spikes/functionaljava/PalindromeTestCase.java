package fm.mox.spikes.functionaljava;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class PalindromeTestCase {
    @Test
    public void testName() {
        TailCall.PalindromePredicate palindromePredicate = new TailCall.PalindromePredicate();
        assertTrue(palindromePredicate.test("aba"));
        assertFalse(palindromePredicate.test("abca"));
    }
}
