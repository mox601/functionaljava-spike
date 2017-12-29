package fm.mox.spikes.functionaljava;

import static java.lang.Character.isLetter;
import static java.lang.Character.toLowerCase;

import java.util.function.Predicate;
import java.util.stream.Stream;

public interface TailCall<T> {

    TailCall<T> apply();

    default boolean isComplete() {
        return false;
    }

    default T result() {
        throw new UnsupportedOperationException();
    }

    default T invoke() {
        return Stream.iterate(this, TailCall::apply)
            .filter(TailCall::isComplete)
            .findFirst()
            .get()
            .result();
    }

    public static <T> TailCall<T> done(final T value) {
        return new TailCall<T>() {

            @Override
            public boolean isComplete() {
                return true;
            }

            @Override
            public T result() {
                return value;
            }

            @Override
            public TailCall<T> apply() {
                throw new UnsupportedOperationException();
            }
        };
    }

    class PalindromePredicate implements Predicate<String> {

        @Override
        public boolean test(String s) {
            return isPalindrome(s, 0, s.length() - 1).invoke();
        }

        private TailCall<Boolean> isPalindrome(String s, int start, int end) {

            while (start < end && !isLetter(s.charAt(start))) {
                start++;
            }
            while (end > start && !isLetter(s.charAt(end))) {
                end--;
            }

            if (start >= end) {
                return done(true);
            }

            if (toLowerCase(s.charAt(start)) != toLowerCase(s.charAt(end))) {
                return done(false);
            }

            int newStart = start + 1;
            int newEnd = end - 1;

            return () -> isPalindrome(s, newStart, newEnd);
        }
    }
}
