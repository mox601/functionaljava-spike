package fm.mox.spikes.functionaljava;

import fj.F;
import fj.Unit;
import fj.data.Natural;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static fj.Show.unlineShow;
import static fj.data.Natural.natural;
import static fj.data.Stream.forever;
import static fj.data.Enumerator.naturalEnumerator;
import static fj.Show.naturalShow;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class StreamTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamTestCase.class);

    @Test
    public void testName() throws Exception {

        final Unit println = unlineShow(naturalShow).println(forever(naturalEnumerator, natural(3)
                .some(), 2).takeWhile(new F<Natural, Boolean>() {
            @Override
            public Boolean f(Natural natural) {

                return natural.intValue() < 100 ? Boolean.TRUE : Boolean.FALSE;

            }
        }));

    }
}
