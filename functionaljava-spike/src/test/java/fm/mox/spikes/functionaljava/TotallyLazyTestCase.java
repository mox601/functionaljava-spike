package fm.mox.spikes.functionaljava;

import com.googlecode.totallylazy.Function;
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

        final Sequence<Integer> integers = Sequences.sequence(1, 2, 3, 4).take(2).forEach(
                new Function<Integer, String>() {
                    @Override
                    public String call(Integer integer) throws Exception {

                        return integer.toString();
                    }
                });
        integers.forEach(integer -> LOGGER.info(integer.toString()));

    }
}
