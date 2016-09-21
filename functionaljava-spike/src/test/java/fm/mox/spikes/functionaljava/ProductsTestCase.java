package fm.mox.spikes.functionaljava;

import fj.P;
import fj.P1;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ProductsTestCase {

    @Test
    public void testName() throws Exception {

        final P1<String> p = P.p("a string");

        assertEquals(p._1(), "a string");

        final P1<Integer> aSize = p.apply(P.p(String::length));

        assertEquals(aSize._1().intValue(), 8);

    }
}
