package fm.mox.spikes.functionaljava;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class TotallyLazyTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TotallyLazyTestCase.class);

    @Test
    public void testName() throws Exception {

        final Sequence<String> integers = Sequences.sequence(1, 2, 3, 4).take(2).map(
                Object::toString);
        integers.forEach(LOGGER::info);

    }
}
