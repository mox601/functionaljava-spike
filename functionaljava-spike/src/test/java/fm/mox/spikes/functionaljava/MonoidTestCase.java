package fm.mox.spikes.functionaljava;

import fj.Monoid;
import fj.P;
import fj.P2;
import fj.Semigroup;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class MonoidTestCase {

    @Test
    public void testSum() throws Exception {
        assertEquals(Monoid.intAdditionMonoid.sum(2, 3).intValue(), 5);
    }

    @Test
    public void testProd() throws Exception {
        assertEquals(Monoid.intMultiplicationMonoid.sum(2, 3).intValue(), 6);
    }

    @Test
    public void testDisj() throws Exception {
        assertEquals(Monoid.disjunctionMonoid.sum(Boolean.FALSE, Boolean.TRUE), Boolean.TRUE);
    }

    @Test
    public void testMonadComposition() throws Exception {
        P2<Integer, Integer> expected = P.p(6, 6);
        Monoid<P2<Integer, Integer>> composed = Monoid.intMultiplicationMonoid
                .compose(Monoid.intAdditionMonoid);
        P2<Integer, Integer> actual = composed.sum(P.p(3, 2)).f(P.p(2, 4));
        assertEquals(actual, expected);
        //TODO get inspiration from fpinscala, scalaz to re-implement and use same things in functionaljava
        //e.g. Lenses, Memoization, Property testing,
        //see https://github.com/bodar/totallylazy
    }
}
