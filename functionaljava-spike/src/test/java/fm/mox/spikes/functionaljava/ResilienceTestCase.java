package fm.mox.spikes.functionaljava;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class ResilienceTestCase {
    @Test
    public void testName() throws Exception {

        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();

        // Get a CircuitBreaker from the CircuitBreakerRegistry with the global default configuration
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("otherName");


        // When I decorate my function
        CheckedFunction0<String> decoratedSupplier = CircuitBreaker
                .decorateCheckedSupplier(circuitBreaker, () -> "This can be any method which returns: 'Hello");

        // and chain an other function with map
        Try<String> result = Try.of(decoratedSupplier)
                .map(value -> value + " world'");

        // Then the Try Monad returns a Success<String>, if all functions ran successfully.

    }
}
