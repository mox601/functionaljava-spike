package fm.mox.spikes.functionaljava;

import fj.data.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class StateTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(StateTestCase.class);

    @Test
    public void testName() throws Exception {

        final State<Object, String> value = State.constant("value");
        final String eval = value.eval(null);
        assertEquals(eval, "value");

    }
}
