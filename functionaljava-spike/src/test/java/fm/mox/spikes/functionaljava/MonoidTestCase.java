package fm.mox.spikes.functionaljava;

import fj.F;
import fj.Monoid;
import fj.P1;
import fj.Semigroup;
import fj.data.List;
import fj.test.Gen;
import fj.test.Property;
import fj.test.Shrink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class MonoidTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonoidTestCase.class);

    @Test
    public void testName() throws Exception {

        final Semigroup<Integer> sumSemigroup = Semigroup.semigroup((i1, i2) -> i1 + i2);
        final Integer zero = 0;
        final Monoid<Integer> integerMonoid = Monoid.monoid(sumSemigroup, zero);

        final int result = integerMonoid.sum(2, 3);
        assertEquals(result, 5);
        //TODO get inspiration from fpinscala, scalaz to re-implement and use same things in functionaljava
        //e.g. Lenses, Memoization, Property testing,
        //see https://github.com/bodar/totallylazy
    }

}
