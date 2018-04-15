package fm.mox.spikes.functionaljava.reader.tostring;

import fj.F;
import fj.data.Reader;
import fm.mox.spikes.functionaljava.reader.User;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class ReaderToStringTest {

    public interface UserReaders {

        Reader<User, User> USER_READER = Reader.unit((F<User, User>) u -> u);

        //read attributes from User aka getters
        static Reader<User, Integer> id() {
            return USER_READER.map(User::getId);
        }

        static Reader<User, String> username() {
            return USER_READER.map(User::getUsername);
        }

        static Reader<User, User> supervisor() {
            return USER_READER.map(User::getSupervisor);
        }
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
}
