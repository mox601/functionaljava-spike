package fm.mox.spikes.functionaljava;

import fj.F2;
import fj.Semigroup;
import fj.data.List;
import fj.data.Validation;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Slf4j
public class ValidationTestCase {

    @Test(expectedExceptions = Exception.class)
    public void testuser() throws Exception {
        final Validation<Exception, User> succeededValidation = makeUser("abc", 1);
        assertTrue(succeededValidation.isSuccess());

        final Validation<Exception, User> notValidName = makeUser("", 1);
        assertTrue(notValidName.isFail());

        final Validation<Exception, User> notValidAge = makeUser("abc", -1);
        assertTrue(notValidAge.isFail());

        final Validation<Exception, User> bothFailed = makeUser("", 189);
        assertTrue(bothFailed.isFail());
        throw bothFailed.fail();
    }

    @Test(expectedExceptions = Exception.class)
    public void testuserL() throws Exception {
        final Validation<List<Exception>, User> succeededValidation = makeUserL("abc", 1);
        assertTrue(succeededValidation.isSuccess());

        final Validation<List<Exception>, User> notValidName = makeUserL("", 1);
        assertTrue(notValidName.isFail());

        final Validation<List<Exception>, User> notValidAge = makeUserL("abc", -1);
        assertTrue(notValidAge.isFail());

        final Validation<List<Exception>, User> bothFailed = makeUserL("", 189);
        assertTrue(bothFailed.isFail());
        List<Exception> fail = bothFailed.fail();
        log.info(fail.toString());
        throw fail.head();
    }

    private static Validation<Exception, User> makeUser(final String name, final int age) {
        final Semigroup<Exception> exceptions =
                Semigroup.semigroup((Exception e, Exception e2) -> new Exception(e.getMessage() + ", " + e2.getMessage()));
        final Validation<Exception, String> nameValidation =
                Validation.condition(name != null && !name.equals(""), new Exception("name must be not null and not empty"), name);
        final Validation<Exception, Integer> ageValidation =
                Validation.condition(age > 0 && age < 120, new Exception("age must be between 0 and 120"), age);
        return nameValidation.accumulate(exceptions, ageValidation, (String s) -> integer -> new User(s, integer));
    }

    private static final Semigroup<List<Exception>> exceptions =
            Semigroup.semigroup(new F2<List<Exception>, List<Exception>, List<Exception>>() {
                @Override
                public List<Exception> f(List<Exception> exceptions, List<Exception> exceptions2) {
                    return exceptions.append(exceptions2);
                }
            });

    private static Validation<List<Exception>, User> makeUserL(final String name, final int age) {
        final Validation<List<Exception>, String> nameValidation =
                Validation.condition(name != null && !name.equals(""), List.single(new Exception("name must be not null and not empty")), name);
        final Validation<List<Exception>, Integer> ageValidation =
                Validation.condition(age > 0 && age < 120, List.single(new Exception("age must be between 0 and 120")), age);
        return nameValidation.accumulate(exceptions, ageValidation, (String s) -> integer -> new User(s, integer));
    }

    @Value
    private static class User {
        private final String name;
        private final int age;
    }
}
