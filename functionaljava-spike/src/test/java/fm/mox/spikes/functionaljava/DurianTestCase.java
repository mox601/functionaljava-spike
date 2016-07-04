package fm.mox.spikes.functionaljava;

import com.diffplug.common.base.Box;
import com.diffplug.common.base.Either;
import com.diffplug.common.base.StringPrinter;
import com.diffplug.common.base.Unhandled;
import org.junit.Test;
import org.testng.Assert;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class DurianTestCase {

    @Test
    public void testLeft() {

        Either<TimeUnit, String> left = Either.createLeft(TimeUnit.DAYS);
        Assert.assertTrue(left.isLeft());
        Assert.assertFalse(left.isRight());
        Assert.assertEquals(TimeUnit.DAYS, left.getLeft());
        Assert.assertEquals(Optional.of(TimeUnit.DAYS), left.asOptionalLeft());
        Assert.assertEquals(Optional.empty(), left.asOptionalRight());
        try {
            left.getRight();
            Assert.fail();
        } catch (Unhandled e) {
        }
        Assert.assertEquals("DAYS", left.fold(TimeUnit::toString, Function.identity()));

        Box.Nullable<TimeUnit> leftSide = Box.Nullable.ofNull();
        Box.Nullable<String> rightSide = Box.Nullable.ofNull();
        left.acceptBoth(leftSide, rightSide, TimeUnit.HOURS, "wahoo");
        Assert.assertEquals(TimeUnit.DAYS, leftSide.get());
        Assert.assertEquals("wahoo", rightSide.get());
    }

    @Test
    public void testRight() {

        Either<TimeUnit, String> right = Either.createRight("word");
        Assert.assertTrue(right.isRight());
        Assert.assertFalse(right.isLeft());
        Assert.assertEquals("word", right.getRight());
        Assert.assertEquals(Optional.of("word"), right.asOptionalRight());
        Assert.assertEquals(Optional.empty(), right.asOptionalLeft());
        try {
            right.getLeft();
            Assert.fail();
        } catch (Unhandled e) {
        }
        Assert.assertEquals("word", right.fold(TimeUnit::toString, Function.identity()));

        Box.Nullable<TimeUnit> leftSide = Box.Nullable.ofNull();
        Box.Nullable<String> rightSide = Box.Nullable.ofNull();
        right.acceptBoth(leftSide, rightSide, TimeUnit.HOURS, "wahoo");
        Assert.assertEquals(TimeUnit.HOURS, leftSide.get());
        Assert.assertEquals("word", rightSide.get());
    }

    private static String staticValue;

    private static String getStaticValue() {

        return staticValue;
    }

    private static void setStaticValue(String value) {

        staticValue = value;
    }

    @Test
    public void testStatic() {

        Box<String> forValue = Box.from(DurianTestCase::getStaticValue,
                DurianTestCase::setStaticValue);
        forValue.set("A");
        Assert.assertEquals("A", forValue.get());
        forValue.set("B");
        Assert.assertEquals("B", forValue.get());
    }

    private String value;

    private String getValue() {

        return value;
    }

    private void setValue(String value) {

        this.value = value;
    }

    @Test
    public void testInstance() {

        Box<String> forValue = Box.from(this, DurianTestCase::getValue, DurianTestCase::setValue);
        forValue.set("A");
        Assert.assertEquals("A", forValue.get());
        forValue.set("B");
        Assert.assertEquals("B", forValue.get());
    }

    @Test
    public void testa() throws Exception {

        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) {

            }
        };
        StringPrinter stringPrinter = new StringPrinter(consumer);

    }
}
