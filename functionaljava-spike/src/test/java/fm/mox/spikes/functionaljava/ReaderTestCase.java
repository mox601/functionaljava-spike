package fm.mox.spikes.functionaljava;

import fj.F;
import fj.Monoid;
import fj.P;
import fj.P2;
import fj.data.Reader;
import fj.data.State;
import fj.data.Writer;
import lombok.Value;
import lombok.experimental.Tolerate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;

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

    private UserRepository userRepository;
    private User bobSupervisor;

    @BeforeMethod
    public void setUp() throws Exception {
        this.bobSupervisor = new User(2, "bob's supervisor");

        this.userRepository = new UserRepository() {
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
    }

    @Test
    public void testR() throws Exception {

        // http://www.davesquared.net/2012/08/reader-monad.html

        Reader<UserRepository, User> readingBobSupervisor = UserReaders
                .getUser(1)
                .map(User::getSupervisor)
                .flatMap(user -> UserReaders.findUser(user.getUsername()));

        User bobSupervisorRead = readingBobSupervisor.f(userRepository);

        assertEquals(bobSupervisorRead, this.bobSupervisor);

        //TODO implement toString with  writer and state monad
    }

    @Test
    public void testEnv() throws Exception {
        IEnv anEnvWithMockUserRepository = () -> () -> userRepository;
        String oneUsername = UserService.getUsername(1).f(anEnvWithMockUserRepository);

        assertEquals(oneUsername, "Bob");
    }

    public interface IEnv {
        IRepositories repositories();
    }

    public interface IRepositories {
        UserRepository userRepository();
    }

    //Reader for dependency injection https://www.youtube.com/watch?v=xPlsVVaMoB0&t=1659s
    public static class Env {
        static final Reader<IEnv, IEnv> ENV_READER =
                Reader.unit((F<IEnv, IEnv>) r -> r);
        static final Reader<IEnv, IRepositories> REPOSITORIES_READER =
                ENV_READER.map(IEnv::repositories);
    }

    public static class Repositories {
        static final Reader<IEnv, UserRepository> USER_REPOSITORY =
                Env.REPOSITORIES_READER.map(IRepositories::userRepository);
    }

    public static class UserRepo {
        static Reader<IEnv, User> get(int id) {
            return Repositories.USER_REPOSITORY.map(userRepository -> userRepository.get(id));
        }
    }

    public static class UserService {
        static Reader<IEnv, String> getUsername(int userId) {
            return UserRepo.get(userId).map(User::getUsername);
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

    public interface UserRepository {
        User get(int id);
        User find(String username);
    }

    public interface UserReaders {
        //unit
        Reader<UserRepository, UserRepository> USER_REPOSITORY_READER =
                Reader.unit(userRepository -> userRepository);

        //read user from UserRepository
        static Reader<UserRepository, User> getUser(int id) {
            return USER_REPOSITORY_READER.map((UserRepository userRepository) -> userRepository.get(id));
        }

        static Reader<UserRepository, User> findUser(String username) {
            return USER_REPOSITORY_READER.map(((UserRepository userRepository) -> userRepository.find(username)));
        }

        //read attributes from User aka getters
        static Reader<User, Integer> id() {
            return Reader.unit(User::getId);
        }

        static Reader<User, String> username() {
            return Reader.unit(User::getUsername);
        }
    }

    @Test
    public void testFib() throws Exception {
        assertEquals(fibMemo1(BigInteger.TEN), BigInteger.valueOf(55L));
    }

    @Test
    public void testFib2() throws Exception {
        assertEquals(fibMemo2(BigInteger.valueOf(55L)), BigInteger.valueOf(139583862445L));
    }

    static BigInteger fibMemo2(BigInteger n) {
        Memo initialized = new Memo()
                .addEntry(BigInteger.ZERO, BigInteger.ZERO)
                .addEntry(BigInteger.ONE, BigInteger.ONE);
        return fibMemo(n).eval(initialized);
    }

    static State<Memo, BigInteger> fibMemo(BigInteger n) {

        F<Memo, Optional<BigInteger>> retrieveIfPresent = (Memo m) -> m.retrieve(n);

        State<Memo, Optional<BigInteger>> initialState = State.gets(retrieveIfPresent);

        Function<BigInteger, State<Memo, BigInteger>> stateFromValue = State::constant;

        F<Optional<BigInteger>, State<Memo, BigInteger>> computeValueIfAbsentFromMap = u -> {

            State<Memo, BigInteger> computedValue =
                    fibMemo(n.subtract(BigInteger.ONE))
                            .flatMap(x -> fibMemo(n.subtract(BigInteger.ONE).subtract(BigInteger.ONE))
                                    .map(x::add)
                                    .flatMap(z -> State.unit(memo -> P.p(memo.addEntry(n, z), z))));

            return u.map(stateFromValue).orElse(computedValue);
        };

        return initialState.flatMap(computeValueIfAbsentFromMap);
    }

    static BigInteger fibMemo1(BigInteger n) {
        Memo initializedMemo = new Memo()
                .addEntry(BigInteger.ZERO, BigInteger.ZERO)
                .addEntry(BigInteger.ONE, BigInteger.ONE);
        return fibMemo(n, initializedMemo)._1();
    }

    static P2<BigInteger, Memo> fibMemo(BigInteger n, Memo memo) {
        return memo.retrieve(n).map(x -> P.p(x, memo)).orElseGet(() -> {
            P2<BigInteger, Memo> minusOne = fibMemo(n.subtract(BigInteger.ONE), memo);
            P2<BigInteger, Memo> minusTwo = fibMemo(n.subtract(BigInteger.ONE).subtract(BigInteger.ONE), memo);
            BigInteger x = minusOne._1().add(minusTwo._1());
            return P.p(x, memo.addEntry(n, x));
        });
    }

    public static class Memo extends HashMap<BigInteger, BigInteger> {

        public Optional<BigInteger> retrieve(BigInteger key) {
            return Optional.ofNullable(super.get(key));
        }

        public Memo addEntry(BigInteger key, BigInteger value) {
            super.put(key, value);
            return this;
        }
    }
}
