package fm.mox.spikes.functionaljava;

import fj.Try;
import fj.data.Validation;
import fj.function.Try1;
import org.junit.Test;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class TryTestCase {

    @Test
    public void testTry() {
        Validation<Exception, Integer> v1 = getExternalValue(2);
        if (v1.isFail()) {
            System.out.println(v1.fail().getMessage());
        } else {
            System.out.println(v1.success());
        }
    }

    private static Validation<Exception, Integer> getExternalValue(Integer i) {
        Try1<Integer, Integer, Exception> t = j -> {
            if (j == 0) {
                throw new NullPointerException("npe");
            } else if (j == 2) {
                throw new ArithmeticException("ae");
            } else {
                return j;
            }
        };
        return Try.f(t).f(i);
    }

}
