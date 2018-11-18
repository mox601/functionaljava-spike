package fm.mox.spikes.functionaljava;

import fj.F;
import fj.data.List;
import fj.data.Natural;
import fj.data.Stream;
import org.testng.annotations.Test;

import static fj.Show.naturalShow;
import static fj.Show.stringShow;
import static fj.Show.unlineShow;
import static fj.data.Enumerator.naturalEnumerator;
import static fj.data.Natural.natural;
import static fj.data.Stream.forever;
import static fj.data.Stream.iterableStream;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class StreamTestCase {

    @Test(enabled = false)
    public void testName() {
        final Natural one = natural(1).some();
        final Stream<Natural> forever = forever(naturalEnumerator, one, 2);
        final F<Natural, Boolean> lessThan =
                natural -> natural.intValue() < 100 ? Boolean.TRUE : Boolean.FALSE;

        unlineShow(naturalShow).println(forever.takeWhile(lessThan));
    }

    @Test
    public void testAStream() {
        final Iterable<Integer> anIterable = List.range(1, 10);
        final Stream<String> fromList = iterableStream(anIterable)
                .cons(100)
                .intersperse(101)
                .map(Object::toString);
        unlineShow(stringShow).println(fromList);
    }
}
