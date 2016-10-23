package fm.mox.spikes.functionaljava;

import com.googlecode.totallylazy.Sequence;
import com.googlecode.totallylazy.Sequences;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Slf4j
public class TotallyLazyTestCase {


    @Test
    public void testName() throws Exception {
        final Sequence<String> integers = Sequences.sequence(1, 2, 3, 4).take(2).map(Object::toString);
        integers.forEach(log::info);
    }
}
