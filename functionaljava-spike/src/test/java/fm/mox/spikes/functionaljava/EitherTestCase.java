package fm.mox.spikes.functionaljava;

import fj.F;
import fj.P1;
import fj.Try;
import fj.data.Either;
import fj.data.List;
import fj.data.Validation;
import fj.function.Try0;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class EitherTestCase {

    @Test
    public void testName() throws Exception {

        final Either<RuntimeException, Integer> a = doThis(2);
        final Either<RuntimeException, Integer> b = doThis(1);
        assertEquals(1, (long) a.right().value());
        assertEquals("", b.left().value().getMessage());

        final int aOrDefault = a.isLeft() ? 0 : a.right().value();
        final int bOrDefault = b.isLeft() ? 0 : b.right().value();
        final int multiplied = aOrDefault * bOrDefault;

        //compose results and exceptions

        //Try from http://danielwestheide.com/blog/2012/12/26/the-neophytes-guide-to-scala-part-6-error-handling-with-try.html

        parseURL("");

    }

    private P1<Validation<MalformedURLException, URL>> parseURL(String s) {
        return Try.f(() -> new URL(s));
    }

    private P1<Validation<IOException, URLConnection>> openConnection(URL u) {
        return Try.f((Try0<URLConnection, IOException>) u::openConnection);
    }

    @Test
    public void catching_other_people_exceptions() {
        final Either<Exception, Integer> result = divide(4, 2);
        final Either<Exception, Integer> failure = divide(4, 0);
        assertEquals((long) 2, (long) result.right().value());
        assertEquals("/ by zero", failure.left().value().getMessage());
    }

    //https://apocalisp.wordpress.com/2008/06/04/throwing-away-throws/
    @Test
    public void testLiftLength() throws Exception {

        final F<String, Either<Exception, Integer>> stringEitherF =
                new ExceptionWrapper<String, Integer>().f(String::length);

        final Either<Exception, Integer> bLength = stringEitherF.f("b");

        assertFalse(bLength.isLeft());
        assertEquals((int) bLength.right().value(), 1);

        assertTrue(stringEitherF.f(null).isLeft());

        final F<String, Either<Exception, Integer>> eagerLiftedLengthF = s -> {
            try {
                return Either.right(length(s));
            } catch (Exception e) {
                return Either.left(e);
            }
        };

        assertTrue(eagerLiftedLengthF.f("a").isRight());

        final Either<Exception, Integer> anException = eagerLiftedLengthF.f(null);
        assertTrue(anException.isLeft());
        assertNotNull(anException.left().value());
        final Exception value = anException.left().value();
        assertTrue(value instanceof NullPointerException);

        final List<Either<Exception, Integer>> eithers = List.arrayList("a", "b", null).map(stringEitherF);

        final String s = eithers.map(Either::toString).foldLeft1((s1, s2) -> s1 + "," + s2);

        System.out.println(s);

    }

    //TODO to work with multiple kind of error nest them on the right Either<E1, Either<E2, A>>

    //TODO show some patterns from https://tersesystems.com/2012/12/27/error-handling-in-scala/
    /*
    *
        TL;DR
        Throw Exception to signal unexpected failure in purely functional code.
        Use Option to return optional values.
        Use Option(possiblyNull) to avoid instances of Some(null).
        Use Either to report expected failure.
        Use Try rather than Either to return exceptions.
        Use Try rather than a catch block for handling unexpected failure.
        Use Try when working with Future.
        Exposing Try in a public API has a similar effect as a checked exception, consider using exceptions instead.
    * */

    private static Integer length(final String a) {
        return a.length();
    }

    private static Either<RuntimeException, Integer> doThis(int i) {
        Either<RuntimeException, Integer> either;
        if (i % 2 == 0) {
            either = Either.right(1);
        } else {
            either = Either.left(new RuntimeException(""));
        }
        return either;
    }

    public static Either<Exception, Integer> divide(int x, int y) {
        Either<Exception, Integer> either;
        try {
            final int div = x / y;
            either = Either.right(div);
        } catch (Exception e) {
            either = Either.left(e);
        }
        return either;
    }

    public static <A> Either<Exception, A> tryMethod(final A a) {
        try {
            return Either.right(a);
        } catch (Exception e) {
            return Either.left(e);
        }
    }

    private static class ExceptionWrapper<IN, OUT> implements F<F<IN, OUT>, F<IN, Either<Exception, OUT>>> {

        @Override
        public F<IN, Either<Exception, OUT>> f(final F<IN, OUT> aFn) {
            return in -> {
                Either<Exception, OUT> outEither;
                try {
                    final OUT output = aFn.f(in);
                    outEither = Either.right(output);
                } catch (final Exception e) { // for npe
                    outEither = Either.left(e);
                }
                return outEither;
            };
        }
    }
}
