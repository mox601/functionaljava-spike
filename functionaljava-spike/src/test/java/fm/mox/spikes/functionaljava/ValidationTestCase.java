package fm.mox.spikes.functionaljava;

import fj.F;
import fj.F2;
import fj.Semigroup;
import fj.data.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ValidationTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ValidationTestCase.class);

    @Test
    public void testuser() throws Exception {

        final Validation<Exception, User> succeededValidation = makeUser("abc", 1);
        assertTrue(succeededValidation.isSuccess());

        final Validation<Exception, User> notValidName = makeUser("", 1);
        assertTrue(notValidName.isFail());

        final Validation<Exception, User> notValidAge = makeUser("abc", -1);
        assertTrue(notValidAge.isFail());

        final Validation<Exception, User> bothFailed = makeUser("", 189);
        assertTrue(bothFailed.isFail());
        assertNotNull(bothFailed.fail().getMessage());

    }

    private static Validation<Exception, User> makeUser(final String name, final int age) {

        final Validation<Exception, String> validatedName = Validation.condition(
                name != null && !name.equals(""), new Exception(
                        "name must be not null and not empty"), name);

        final Validation<Exception, Integer> validatedAge = Validation.condition(
                age > 0 && age < 120, new Exception("age must be between 0 and 120"), age);

        final Semigroup<Exception> exceptionsSemigroup = Semigroup.semigroup(
                new F2<Exception, Exception, Exception>() {
                    @Override
                    public Exception f(Exception e, Exception e2) {

                        return new Exception(e.getMessage() + ", " + e2.getMessage());
                    }
                });

        final F<String, F<Integer, User>> userBuilderFunc = new F<String, F<Integer, User>>() {
            @Override
            public F<Integer, User> f(final String s) {

                return new F<Integer, User>() {
                    @Override
                    public User f(final Integer integer) {

                        return new User(s, integer);
                    }
                };
            }
        };

        return validatedName.accumulate(exceptionsSemigroup, validatedAge, userBuilderFunc);

    }

    private static class User {

        private final String name;
        private final int age;

        public User(String name, int age) {

            this.name = name;
            this.age = age;
        }
    }
}
