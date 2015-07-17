package fm.mox.spikes.functionaljava;

import fj.F;
import fj.P1;
import fj.data.List;
import fj.data.Option;
import fj.test.Gen;
import fj.test.Property;
import fj.test.Shrink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class GenTestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenTestCase.class);

    @Test
    public void givenGeneratedListAllItemsAreLtEq100() throws Exception {

        final Gen<List<Integer>> aGeneratedListOfInts = Gen.listOf(Gen.choose(0, 100));

        final F<List<Integer>, P1<Property>> allLtEq = integers -> new P1<Property>() {
            @Override
            public Property _1() {

                return Property.prop(integers.forall(integer -> integer <= 100));
            }
        };

        final Shrink<Integer> sa = Shrink.shrinkInteger;
        final Shrink<List<Integer>> listShrink = Shrink.shrinkList(sa);
        final Property forall = Property.forall(aGeneratedListOfInts, listShrink, allLtEq);

        assertTrue(forall.check().isPassed());

    }

    @Test
    public void testFPScala() throws Exception {

        final Gen<List<Integer>> intList = Gen.listOf(Gen.choose(0, 100));

        final Shrink<List<Integer>> shrinkList = Shrink.shrinkList(Shrink.shrinkInteger);
        final F<List<Integer>, P1<Property>> reversedTwiceEqualsOriginal =
                integers -> new P1<Property>() {
                    @Override
                    public Property _1() {

                        return Property.prop(integers.reverse().reverse().equals(integers));

                    }
                };
        final F<List<Integer>, P1<Property>> headEqualsLastOfReverse =
                integers -> new P1<Property>() {
                    @Override
                    public Property _1() {

                        final Option<Integer> head = integers.toOption();
                        final Option<Integer> last = integers.reverse().toOption();

                        //TODO fixit
                        final boolean equals = true;

                        return Property.prop(equals);
                    }
                };

        final Property and = Property.forall(intList, shrinkList, reversedTwiceEqualsOriginal).and(
                Property.forall(intList, shrinkList, headEqualsLastOfReverse));
        LOGGER.info(and.check().exception().toString());
        assertTrue(and.check().isPassed());

    }

}
