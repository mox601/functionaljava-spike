package fm.mox.spikes.bettermonads;

import com.jasongoodwin.monads.Try;
import com.jasongoodwin.monads.TryConsumer;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Slf4j
public class BetterMonadsTestCase {

    @Test
    public void itShouldBeSuccessOnSuccess() throws Throwable {
        Try<String> t = Try.ofFailable(() -> "hey");

        assertTrue(t.isSuccess());
    }

    @Test
    public void itShouldHoldValueOnSuccess() throws Throwable {
        Try<String> t = Try.ofFailable(() -> "hey");

        assertEquals("hey", t.get());
    }

    @Test
    public void itShouldMapOnSuccess() throws Throwable {
        Try<String> t = Try.ofFailable(() -> "hey");
        Try<Integer> intT = t.map((x) -> 5);
        intT.get();

        assertEquals(5, intT.get().intValue());
    }

    @Test
    public void itShouldFlatMapOnSuccess() throws Throwable {
        Try<String> t = Try.ofFailable(() -> "hey");
        Try<Integer> intT = t.flatMap((x) -> Try.ofFailable(() -> 5));
        intT.get();

        assertEquals(5, intT.get().intValue());
    }

    @Test
    public void itShouldOrElseOnSuccess() {
        String t = Try.ofFailable(() -> "hey").orElse("jude");

        assertEquals("hey", t);
    }

    @Test
    public void itShouldReturnValueWhenRecoveringOnSuccess() {
        String t = Try.ofFailable(() -> "hey").recover((e) -> "jude");

        assertEquals("hey", t);
    }

    @Test
    public void itShouldReturnValueWhenRecoveringWithOnSuccess() throws Throwable {
        String t = Try.ofFailable(() -> "hey")
                .recoverWith((x) -> Try.ofFailable(() -> "Jude")).get();

        assertEquals("hey", t);
    }

    @Test
    public void itShouldOrElseTryOnSuccess() throws Throwable {
        Try<String> t = Try.ofFailable(() -> "hey").orElseTry(() -> "jude");

        assertEquals("hey", t.get());
    }

    @Test
    public void itShouldBeFailureOnFailure() {
        Try<String> t = Try.ofFailable(() -> {
            throw new Exception("e");
        });
        assertFalse(t.isSuccess());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void itShouldThrowExceptionOnGetOfFailure() throws Throwable {
        Try.ofFailable(() -> {
            throw new IllegalArgumentException("e");
        }).get();
    }

    @Test
    public void itShouldMapOnFailure() {
        Try<String> t = Try.ofFailable(() -> {
            throw new Exception("e");
        }).map((x) -> "hey" + x);

        assertFalse(t.isSuccess());
    }

    @Test
    public void itShouldFlatMapOnFailure() {
        Try<String> t = Try.ofFailable(() -> {
            throw new Exception("e");
        }).flatMap((x) -> Try.ofFailable(() -> "hey"));

        assertFalse(t.isSuccess());
    }

    @Test
    public void itShouldOrElseOnFailure() {
        String t = Try.<String>ofFailable(() -> {
            throw new IllegalArgumentException("e");
        }).orElse("jude");

        assertEquals("jude", t);
    }

    @Test
    public void itShouldOrElseTryOnFailure() throws Throwable {
        Try<String> t = Try.<String>ofFailable(() -> {
            throw new IllegalArgumentException("e");
        }).orElseTry(() -> "jude");

        assertEquals("jude", t.get());
    }

    @Test
    public void itShouldReturnRecoverValueWhenRecoveringOnFailure() {
        String t = Try.ofFailable(() -> "hey").<String>map((x) -> {
            throw new Exception("fail");
        }).recover((e) -> "jude");

        assertEquals("jude", t);
    }

    @Test
    public void itShouldReturnValueWhenRecoveringWithOnFailure() throws Throwable {
        String t = Try.<String>ofFailable(() -> {
            throw new Exception("oops");
        }).recoverWith((x) ->
                Try.ofFailable(() -> "Jude")
        ).get();

        assertEquals("Jude", t);
    }

    @Test
    public void itShouldHandleComplexChaining() throws Throwable {
        Try.ofFailable(() -> "1").<Integer>flatMap((x) -> Try.ofFailable(() -> Integer.valueOf(x))).recoverWith((t) -> Try.successful(1));
    }

    @Test
    public void itShouldPassFailureIfPredicateIsFalse() throws Throwable {
        Try t1 = Try.ofFailable(() -> {
            throw new RuntimeException();
        }).filter(o -> false);

        Try t2 = Try.ofFailable(() -> {
            throw new RuntimeException();
        }).filter(o -> true);

        assertEquals(t1.isSuccess(), false);
        assertEquals(t2.isSuccess(), false);
    }

    @Test
    public void isShouldPassSuccessOnlyIfPredicateIsTrue() throws Throwable {
        Try t1 = Try.ofFailable(() -> "yo mama").filter(s -> s.length() > 0);
        Try t2 = Try.ofFailable(() -> "yo mama").filter(s -> s.length() < 0);

        assertEquals(t1.isSuccess(), true);
        assertEquals(t2.isSuccess(), false);
    }

    @Test
    public void itShouldReturnEmptyOptionalIfFailureOrNullSuccess() throws Throwable {
        Optional<String> opt1 = Try.<String>ofFailable(() -> {
            throw new IllegalArgumentException("Expected exception");
        }).toOptional();
        Optional<String> opt2 = Try.<String>ofFailable(() -> null).toOptional();

        assertFalse(opt1.isPresent());
        assertFalse(opt2.isPresent());
    }

    @Test
    public void isShouldReturnTryValueWrappedInOptionalIfNonNullSuccess() throws Throwable {
        Optional<String> opt1 = Try.ofFailable(() -> "yo mama").toOptional();
        assertTrue(opt1.isPresent());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void itShouldThrowExceptionFromTryConsumerOnSuccessIfSuccess() throws Throwable {
        Try<String> t = Try.ofFailable(() -> "hey");
        t.onSuccess(s -> {
            throw new IllegalArgumentException("Should be thrown.");
        });
    }

    @Test
    public void itShouldNotThrowExceptionFromTryConsumerOnSuccessIfFailure() throws Throwable {
        Try<String> t = Try.ofFailable(() -> {
            throw new IllegalArgumentException("Expected exception");
        });
        t.onSuccess(s -> {
            throw new IllegalArgumentException("Should NOT be thrown.");
        });
    }

    @Test
    public void itShouldNotThrowExceptionFromTryConsumerOnFailureIfSuccess() throws Throwable {
        Try<String> t = Try.ofFailable(() -> "hey");
        t.onFailure(s -> {
            throw new IllegalArgumentException("Should NOT be thrown.");
        });
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void itShouldThrowExceptionFromTryConsumerOnFailureIfFailure() throws Throwable {
        Try<String> t = Try.ofFailable(() -> {
            throw new IllegalArgumentException("Expected exception");
        });
        t.onFailure(s -> {
            throw new IllegalArgumentException("Should be thrown.");
        });
    }

    @Test
    public void testDiv() throws Throwable {

        Try<String> successfulA = Try.successful("a")
                .onSuccess((TryConsumer<String, Throwable>) log::info)
                .onFailure(t -> log.error("error", t));
        successfulA.get();

        Try<Integer> aLength = successfulA.map(String::length);

        assertEquals(aLength.get(), Integer.valueOf(1));

    }

    private static Try<Integer> tryDivide() {
        return null;
    }

    @Test
    public void testName() throws Exception {
        List<String> input = Arrays.asList("aaa", "b", "abc  ", "  qqq  ");
        Map<Boolean, List<Try<String>>> result = input.stream()
                .map(Try::successful).map(t -> t.map(BetterMonadsTestCase::doStuff))
                .collect(Collectors.partitioningBy(Try::isSuccess));
        log.info(result.get(true).stream().map(t -> t.orElse(null))
                .collect(Collectors.joining(",")));
        result.get(false).forEach((Try<String> t) -> t.onFailure((x) -> {
            log.error("e", x);
        }));
    }

    static String doStuff(String s) {
        if (s.startsWith("a")) {
            throw new IllegalArgumentException("Incorrect string: " + s);
        }
        return s.trim();
    }
}
