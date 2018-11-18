package fm.mox.spikes.functionaljava;

import fj.Show;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Slf4j
public class ShowTestCase {

    @Test
    public void testName() {
        final Show<Integer> integerShow = Show.showS(Object::toString);
        assertEquals(integerShow.showS(1), "1");
    }
}
