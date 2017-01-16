package fm.mox.spikes.functionaljava;

import fj.data.Option;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class OptionTestCase {

    @Test
    public void option_test_success() {
        assertEquals(2.0, divide(4.0, 2).some(), 0.1);
    }

    @Test
    public void option_test_failure() {
        assertEquals(Option.none(), divide(4.0, 0));
    }

    public static Option<Double> divide(double x, double y) {
        if (y == 0) {
            return Option.none();
        }
        return Option.some(x / y);
    }
}
