package fm.mox.spikes.functionaljava;

import fj.F;
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

        final P1<Integer> aSize = p.apply(new P1<F<String, Integer>>() {
            @Override
            public F<String, Integer> _1() {

                return String::length;
            }
        });

        assertEquals(aSize._1().intValue(), 8);

    }
}
