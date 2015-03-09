package fm.mox.spikes.functionaljava;

import fj.F;
import fj.data.Natural;
import org.testng.annotations.Test;

import static fj.Show.naturalShow;
import static fj.Show.unlineShow;
import static fj.data.Enumerator.naturalEnumerator;
import static fj.data.Natural.natural;
import static fj.data.Stream.forever;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class StreamTestCase {

    @Test
    public void testName() throws Exception {

        unlineShow(naturalShow).println(forever(naturalEnumerator, natural(1).some(), 2).takeWhile(
                new F<Natural, Boolean>() {
                    @Override
                    public Boolean f(Natural natural) {

                        return natural.intValue() < 100 ? Boolean.TRUE : Boolean.FALSE;

                    }
                }));

    }
}
