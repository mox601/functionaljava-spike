package fm.mox.spikes.functionaljava;

import fj.Monoid;
import fj.data.Writer;
import lombok.Value;
import lombok.experimental.Tolerate;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class WriterTestCase {

    //the Writer monad let us create and accumulate a log across multiple function calls
    @Test
    public void testLogWithWriter() throws Exception {
        User aUser = new User(2, "makeSomethingElse");
        Writer<String, User> toStringW = LogWriters
                .makeSomething(aUser)
                .flatMap(user -> LogWriters.makeSomethingElse(aUser));
        assertEquals(toStringW.log(), "did this\ndid some other thing\n");
    }

    @Value
    private static class User {
        int id;
        String username;
        User supervisor;
        @Tolerate
        private User(final int id, final String username) {
            this(id, username, null);
        }
    }

    public interface LogWriters {
        static Writer<String, User> makeSomething(User user) {
            return Writer.unit(user, "did this\n", Monoid.stringMonoid);
        }

        static Writer<String, User> makeSomethingElse(User user) {
            return Writer.unit(user, "did some other thing\n", Monoid.stringMonoid);
        }
    }
}
