package fm.mox.spikes.functionaljava;

import fj.Monoid;
import fj.P;
import fj.Semigroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class MonoidTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonoidTestCase.class);
    private Monoid<Integer> intSum;
    private Monoid<Integer> intProd;
    private Monoid<Boolean> disjunctionMonoid;

    @BeforeMethod
    public void setUp() throws Exception {
        intSum = Monoid.monoid(Semigroup.intAdditionSemigroup, 0);
        intProd = Monoid.monoid(Semigroup.intMultiplicationSemigroup, 1);
        disjunctionMonoid = Monoid.monoid(Semigroup.disjunctionSemigroup, Boolean.FALSE);
    }

    @Test
    public void testSum() throws Exception {
        assertEquals(intSum.sum(2, 3).intValue(), 5);
    }

    @Test
    public void testProd() throws Exception {
        assertEquals(intProd.sum(2, 3).intValue(), 6);
    }

    @Test
    public void testDisj() throws Exception {
        assertEquals(disjunctionMonoid.sum(Boolean.FALSE, Boolean.TRUE), Boolean.TRUE);
    }

    @Test
    public void testMonadComposition() throws Exception {
        assertEquals(intProd.compose(intSum).sum(P.p(1, 2)).f(P.p(3, 4)), P.p(3, 6));
        //TODO get inspiration from fpinscala, scalaz to re-implement and use same things in functionaljava
        //e.g. Lenses, Memoization, Property testing,
        //see https://github.com/bodar/totallylazy
    }

}
