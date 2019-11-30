package fm.mox.spikes.functionaljava.reader;

import fj.F;
import fj.data.Reader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Map;

import static fm.mox.spikes.functionaljava.reader.ReaderTestCase.Env.REPOSITORIES_READER;
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

    private UserRepository userRepository;
    private User bobSupervisor;
    private IEnv anEnvWithMockUserRepository;

    @BeforeMethod
    public void setUp() {

        bobSupervisor = new User(2, "bob's supervisor");

        userRepository = new UserRepository() {
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

        anEnvWithMockUserRepository = () -> () -> userRepository;
    }

    @Test
    public void testR() {

        // http://www.davesquared.net/2012/08/reader-monad.html

        Reader<IEnv, String> userSupervisorName = UserService.getUserSupervisor(1)
                .map(User::getUsername);

        String bobSupervisorsName = userSupervisorName.f(anEnvWithMockUserRepository);

        assertEquals(bobSupervisorsName, this.bobSupervisor.getUsername());

        //TODO implement toString with  writer and state monad
    }

    @Test
    public void testEnv() {
        String oneUsername = UserService.getUsername(1).f(anEnvWithMockUserRepository);
        assertEquals(oneUsername, "Bob");
    }

    public interface IEnv {
        IRepositories repositories();
    }

    public interface IRepositories {
        UserRepository userRepository();
    }

    //todo config framework using reader monad
    //Reader for dependency injection https://www.youtube.com/watch?v=xPlsVVaMoB0&t=1659s
    //https://www.originate.com/thinking/stories/reader-monad-for-dependency-injection/

    public static class Env {

        static final Reader<IEnv, IEnv> ENV_READER = Reader.unit(r -> r);

        static final Reader<IEnv, IRepositories> REPOSITORIES_READER =
                ENV_READER.map(IEnv::repositories);
    }

    public static class Repositories {
        static final Reader<IEnv, UserRepository> USER_REPOSITORY =
                REPOSITORIES_READER.map(IRepositories::userRepository);
    }

    public static class UserRepo {
        static Reader<IEnv, User> get(int id) {
            return Repositories.USER_REPOSITORY.map(userRepository -> userRepository.get(id));
        }
        static Reader<IEnv, User> find(String username) {
            return Repositories.USER_REPOSITORY.map(userRepository -> userRepository.find(username));
        }
    }

    public static class UserService {

        static Reader<IEnv, String> getUsername(int userId) {
            return UserRepo.get(userId).map(User::getUsername);
        }

        static Reader<IEnv, User> getUserSupervisor(int userId) {
            return UserRepo.get(userId).map(User::getSupervisor);
        }
    }
}
