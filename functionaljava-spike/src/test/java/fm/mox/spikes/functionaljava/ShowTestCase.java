package fm.mox.spikes.functionaljava;

import fj.Show;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ShowTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShowTestCase.class);

    @Test
    public void testName() throws Exception {

        final Show<Integer> integerShow = Show.showS(Object::toString);

        assertEquals(integerShow.showS(1), "1");

    }
}
