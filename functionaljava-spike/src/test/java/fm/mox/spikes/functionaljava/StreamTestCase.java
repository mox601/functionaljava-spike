package fm.mox.spikes.functionaljava;

import fj.F;
import fj.data.Natural;
import fj.data.Stream;
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

        final Natural one = natural(1).some();
        final Stream<Natural> forever = forever(naturalEnumerator, one, 2);

        final F<Natural, Boolean> lessThan =
                natural -> natural.intValue() < 100 ? Boolean.TRUE : Boolean.FALSE;

        unlineShow(naturalShow).println(forever.takeWhile(lessThan));

    }
}
