package fm.mox.spikes.vavr;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;
import io.vavr.control.Validation;
import io.vavr.test.Arbitrary;
import io.vavr.test.Property;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class VavrTest {

    // it adheres to the requirement of a monad to maintain computational context when calling .map.
    // In terms of an Option, this means that calling .map on a Some will result in a Some,
    // and calling .map on a None will result in a None. In the Java Optional example above,
    // that context changed from a Some to a None.
    @Test(expectedExceptions = NullPointerException.class)
    public void npe() throws Exception {
        Option<String> maybeFoo = Option.of("foo");

        assertEquals("foo", maybeFoo.get());

        maybeFoo.map(s -> (String) null)
                .map(s -> s.toUpperCase() + "bar");
    }

    // This may seem to make Option useless, but it actually forces you to pay attention to possible
    // occurrences of null and deal with them accordingly instead of unknowingly accepting them.
    // The correct way to deal with occurrences of null is to use flatMap.
    @Test
    public void carefully() throws Exception {
        Option<String> maybeFooBar = Option.of("foo")
                .map(s -> (String) null)
                .flatMap(s -> Option.of(s)
                        .map(t -> t.toUpperCase() + "bar"));
        assertTrue(maybeFooBar.isEmpty());
    }

    //    http://blog.javaslang.io/the-agonizing-death-of-an-astronaut/
    @Test
    public void alternative() throws Exception {
        Option<String> maybeFooBar = Option.of("foo")
                .flatMap(s -> Option.of((String) null))
                .map(s -> s.toUpperCase() + "bar");

        assertTrue(maybeFooBar.isEmpty());
    }

    @Test
    public void name() {
        Option<String> maybeFooBar = Option.of("foo")
                .map(s -> (String) null)
                .flatMap(s -> Option.of(s)
                        .map(t -> t.toUpperCase() + "bar"));
        assertTrue(maybeFooBar.isEmpty());
    }

    @Test
    public void trya() {
        Try<Integer> divide = divide(1, 0);
        assertEquals(1, divide.getOrElse(() -> 1).intValue());
    }

    @Test(expectedExceptions = ArithmeticException.class)
    public void testException() {
        Try<Integer> divide = divide(1, 0);
        divide.get();
    }

    @Test
    public void testLogging() {
        divide(1, 0)
                .onFailure(t -> log.error(t.getMessage(), t));
        log.info("post division");
    }

    @Test
    public void testLoggingWithTryCatch() {
        try {
            divideWithoutTryCatch(1, 0);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
        log.info("post division");
    }

    private int divideWithoutTryCatch(int dividend, int divisor) {
        log.info("pre division");
        return dividend / divisor;
    }

    private Try<Integer> divide(Integer dividend, Integer divisor) {
        return Try.of(() -> {
            log.info("pre division");
            return dividend / divisor;
        });
    }

    //    @Test(enabled = false)
    public void prop() throws Exception {
        // square(int) >= 0: OK, passed 1000 tests.
        Property.def("square(int) >= 0")
                .forAll(Arbitrary.integer())
                .suchThat(i -> i * i >= 0)
                .check()
                .assertIsSatisfied();
    }

    @Test
    public void lifting() throws Exception {

        Function2<Integer, Integer, Integer> divide = (a, b) -> a / b;
        Function2<Integer, Integer, Option<Integer>> safeDivide = Function2.lift(divide);

        Option<Integer> i1 = safeDivide.apply(1, 0);
        assertEquals(Option.none(), i1);

        Option<Integer> i2 = safeDivide.apply(4, 2);
        assertEquals(Option.of(2), i2);

        Function2<Integer, Integer, Integer> sum = (a, b) -> a + b;
        Function1<Integer, Integer> add2 = sum.curried().apply(2);

        assertEquals(6, add2.apply(4).intValue());

    }

    //TODO https://koziolekweb.pl/2016/06/18/pattern-matching-w-javie-z-javaslang-ii/
    @Test
    public void testMatch() throws Exception {
        String s = Match(1).of(
                Case($(1), "one"),
                Case($(2), "two"),
                Case($(), "?")
        );
        assertEquals("one", s);

        Option<String> v = Match(1).option(
                Case($(0), "zero")
        );
        assertEquals(Option.<String>none(), v);

        /*String x = Match(1).of(
                Case(is(1), "one"),
                Case(is(2), "two"),
                Case($(), "?")
        );
        assertEquals("one", x);

        Patterns.$Valid()
        Number obj = 8;
        Number plusOne = Match(obj).of(
                Case(instanceOf(Integer.class), i -> i + 1),
                Case(instanceOf(Double.class), d -> d + 1),
                Case($(), o -> { throw new NumberFormatException(); })
        );
        assertEquals(9, plusOne);
        */

    }

    @Test
    public void validator() throws Exception {

        // Valid(Person(John Doe, 30))
        Validation<Seq<String>, Person> valid = PersonValidator.validatePerson("John Doe", 30);
        assertTrue(valid.isValid());
        assertEquals(new Person("John Doe", 30), valid.get());

        // Invalid(List(Name contains invalid characters: '!4?', Age must be greater than 0))
        Validation<Seq<String>, Person> invalid = PersonValidator.validatePerson("John? Doe!4", -1);
        assertTrue(invalid.isInvalid());
        assertEquals(List.of("Name contains invalid characters: '!4?'", "Age must be at least 0"), invalid.getError());

    }

    public static class PersonValidator {

        private static final String VALID_NAME_CHARS = "[a-zA-Z ]";
        private static final int MIN_AGE = 0;

        static Validation<Seq<String>, Person> validatePerson(String name, int age) {
            return Validation.combine(validateName(name), validateAge(age)).ap(Person::new);
        }

        private static Validation<String, String> validateName(String name) {
            return CharSeq.of(name).replaceAll(VALID_NAME_CHARS, "").transform(seq -> seq.isEmpty()
                    ? Validation.valid(name)
                    : Validation.invalid("Name contains invalid characters: '" + seq.distinct().sorted() + "'"));
        }

        private static Validation<String, Integer> validateAge(int age) {
            return age < MIN_AGE ? Validation.invalid("Age must be at least " + MIN_AGE) : Validation.valid(age);
        }
    }

    @Value
    public static class Person {
        String name;
        int age;
    }
}
