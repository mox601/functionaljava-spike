package fm.mox.spikes.functionaljava;

import fj.F;
import fj.P;
import fj.P1;
import fj.data.List;
import fj.data.Option;
import fj.data.Stream;
import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;
import fj.test.Shrink;
import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class GenTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenTestCase.class);

    @Test(enabled = false)
    public void givenGeneratedListAllItemsAreLtEq100() throws Exception {

        final F<List<Integer>, P1<Property>> allLtEq =
                integers -> P.p(Property.prop(integers.forall(integer -> integer <= 100)));

        final Shrink<List<Integer>> listShrink = Shrink.shrinkList(Shrink.shrinkInteger);

        final Property forall = Property.forall(Gen.listOf(Gen.choose(0, 100)), listShrink, allLtEq);

        assertTrue(forall.check().isPassed());

    }

    @Test(enabled = false)
    public void testFPScala() throws Exception {

        final Gen<List<Integer>> ints = Gen.listOf(Gen.choose(0, 100));

        final Shrink<List<Integer>> shrinkListInts = Shrink.shrinkList(Shrink.shrinkInteger);

        final F<List<Integer>, P1<Property>> reversedTwiceEqualsOriginal =
                integers -> P.p(Property.prop(integers.reverse().reverse().equals(integers)));

        final F<List<Integer>, P1<Property>> headEqualsLastOfReverse =
                integers -> {
                    boolean isHeadEqualsToLastOfReverse = true;
                    final Option<Integer> headOpt = integers.headOption();
                    if (headOpt.isSome()) {
                        final Integer head = headOpt.some();
                        final Integer lastOfReverse = integers.reverse().last();
                        isHeadEqualsToLastOfReverse = head.equals(lastOfReverse);
                    }
                    return P.p(Property.prop(isHeadEqualsToLastOfReverse));
                };

        final Property reversedTwiceProp = Property.forall(ints, shrinkListInts, reversedTwiceEqualsOriginal);
        final Property oppositeEqualsProp = Property.forall(ints, shrinkListInts, headEqualsLastOfReverse);

        final Property and = reversedTwiceProp.and(oppositeEqualsProp);

        final CheckResult check = and.check();
        LOGGER.info(check.exception().toString());
        assertTrue(check.isPassed());

    }

    @Test(enabled = false)
    public void testName() throws Exception {

        final Gen<User> userGen = Gen.gen(integer -> rand -> {
            final int nameLength = rand.choose(0, 20);
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < nameLength; i++) {
                //TODO random character
                final String str = "a";
                sb.append(str);
            }
            final String name = sb.toString();
            return new User(name);
        });

        final Shrink<User> shrinkUser = Shrink.shrink(Stream::single);

        final F<User, P1<Property>> prp = user -> P.p(Property.prop(user.getName().length() < 10));

        final Property oppositeEqualsProp = Property.forall(userGen, shrinkUser, prp);

        final CheckResult check = oppositeEqualsProp.check();

        assertTrue(check.isPassed());

    }

    @Value
    public static class User {
        private final String name;
        public int count() {
            return this.name.length();
        }
    }
}
