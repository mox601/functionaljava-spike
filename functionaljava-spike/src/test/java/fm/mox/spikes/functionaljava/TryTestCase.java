package fm.mox.spikes.functionaljava;

import fj.Try;
import fj.data.Validation;
import fj.function.Try1;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Slf4j
public class TryTestCase {

    @Test
    public void testTry() {
        Validation<Exception, Integer> v1 = getExternalValue(2);
        if (v1.isFail()) {
            log.info(v1.fail().getMessage());
        } else {
            log.info(v1.success().toString());
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
