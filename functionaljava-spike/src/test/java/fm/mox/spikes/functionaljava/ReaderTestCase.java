package fm.mox.spikes.functionaljava;

import fj.Monoid;
import fj.P;
import fj.P2;
import fj.data.Reader;
import fj.data.State;
import fj.data.Writer;
import lombok.Value;
import lombok.experimental.Tolerate;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class ReaderTestCase {

    //http://www.davesquared.net/2012/08/reader-monad.html
    //http://blog.ssanj.net/posts/2014-09-23-A-Simple-Reader-Monad-Example.html
    //http://blog.originate.com/blog/2013/10/21/reader-monad-for-dependency-injection/
    //https://gist.github.com/loicdescotte/4044169
    //http://stackoverflow.com/questions/12792595/how-to-convert-this-map-flatmap-into-a-for-comprehension-in-scala
    //http://troydm.github.io/blog/2015/01/25/write-you-a-monad-for-no-particular-reason-at-all/
    //https://gist.github.com/danhyun/fda27d5682b7dbed151b
    //
    //

    @Test
    public void testR() throws Exception {

        final User bobSupervisor = new User(2, "bob's supervisor");

        UserRepository userRepository = new UserRepository() {
            @Override
            public User get(int id) {
                return new User(1, "Bob", bobSupervisor);
            }

            @Override
            public User find(String username) {
                if (username.equals("Bob")) {
                    return new User(1, "Bob", bobSupervisor);
                } else {
                    return bobSupervisor;
                }
            }
        };

        // http://www.davesquared.net/2012/08/reader-monad.html

        Reader<UserRepository, User> readingBobSupervisor = UserReaders
                .getUser(1)
                .map(User::getSupervisor)
                .flatMap(user -> UserReaders.findUser(user.getUsername()));

        User bobSupervisorRead = readingBobSupervisor.f(userRepository);

        assertEquals(bobSupervisorRead, bobSupervisor);


        //TODO implement toString with  writer and state monad
    }

    @Test
    public void testToStringWithReader() throws Exception {

        User aUser = new User(2, "username");

        //to string example

        Reader<User, String> toStringReader = UserReaders.id()
                .flatMap(id -> UserReaders.username().map(username -> id + " " + username));

        String asString = toStringReader.f(aUser);

        assertEquals(asString, "2 username");

    }

    //the Writer monad let us create and accumulate a log across multiple function calls
    @Test
    public void testLogWithWriter() throws Exception {

        User aUser = new User(2, "makeSomethingElse");

        Writer<String, User> toStringW = LogWriters
                .makeSomething(aUser)
                .flatMap(user -> LogWriters.makeSomethingElse(aUser));

        assertEquals(toStringW.log(), "did thisdid some other thing");
    }

    @Test
    public void testToStringWithState() throws Exception {

        State<String, String> st1 = State.<String>init()
            .flatMap(s -> State.unit(s2 -> P.p("Batman", "Hello " + s)));
        P2<String, String> robin = st1.run("Robin");
        System.out.println(robin);
        String aRobin = st1.eval("Robin");
        System.out.println(aRobin);


        //TODO implement toString with state
        State<User, StringBuilder> id = null;
        State<User, StringBuilder> idAndUsername = null;

    }

    public interface UserRepository {
        User get(int id);

        User find(String username);
    }

    public interface LogWriters {

        static Writer<String, User> makeSomething(User user) {
            return Writer.unit(user, "did this", Monoid.stringMonoid);
        }

        static Writer<String, User> makeSomethingElse(User user) {
            return Writer.unit(user, "did some other thing", Monoid.stringMonoid);
        }
    }

    public interface UserReaders {
        static Reader<UserRepository, User> getUser(int id) {
            return Reader.unit((UserRepository userRepository) -> userRepository.get(id));
        }

        static Reader<UserRepository, User> findUser(String username) {
            return Reader.unit((UserRepository userRepository) -> userRepository.find(username));
        }

        static Reader<User, String> id() {
            return Reader.unit(user -> user.getId() + "");
        }

        static Reader<User, String> username() {
            return Reader.unit(User::getUsername);
        }
    }

    @Value
    private static class User {

        private final int id;
        private final String username;
        private final User supervisor;

        @Tolerate
        private User(final int id, final String username) {
            this(id, username, null);
        }
    }
}
