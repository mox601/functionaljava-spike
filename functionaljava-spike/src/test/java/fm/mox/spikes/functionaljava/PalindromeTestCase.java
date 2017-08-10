package fm.mox.spikes.functionaljava;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class PalindromeTestCase {
    @Test
    public void testName() throws Exception {
        TailCall.PalindromePredicate palindromePredicate = new TailCall.PalindromePredicate();
        assertTrue(palindromePredicate.test("aba"));
        assertFalse(palindromePredicate.test("abca"));
    }
}
