package fm.mox.spikes.durian;

import com.diffplug.common.base.Box;
import com.diffplug.common.base.Either;
import com.diffplug.common.base.StringPrinter;
import com.diffplug.common.base.Unhandled;
import lombok.Data;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Data
public class DurianTestCase {

    private String value;

    @Test
    public void testLeft() {

        Either<TimeUnit, String> left = Either.createLeft(TimeUnit.DAYS);
        assertTrue(left.isLeft());
        assertFalse(left.isRight());
        assertEquals(TimeUnit.DAYS, left.getLeft());
        assertEquals(Optional.of(TimeUnit.DAYS), left.asOptionalLeft());
        assertEquals(Optional.empty(), left.asOptionalRight());
        try {
            left.getRight();
            Assert.fail();
        } catch (Unhandled e) {
        }
        assertEquals("DAYS", left.fold(TimeUnit::toString, Function.identity()));

        Box.Nullable<TimeUnit> leftSide = Box.Nullable.ofNull();
        Box.Nullable<String> rightSide = Box.Nullable.ofNull();
        left.acceptBoth(leftSide, rightSide, TimeUnit.HOURS, "wahoo");
        assertEquals(TimeUnit.DAYS, leftSide.get());
        assertEquals("wahoo", rightSide.get());
    }

    @Test
    public void testRight() {

        Either<TimeUnit, String> right = Either.createRight("word");
        assertTrue(right.isRight());
        assertFalse(right.isLeft());
        assertEquals("word", right.getRight());
        assertEquals(Optional.of("word"), right.asOptionalRight());
        assertEquals(Optional.empty(), right.asOptionalLeft());
        try {
            right.getLeft();
            Assert.fail();
        } catch (Unhandled e) {
        }
        assertEquals("word", right.fold(TimeUnit::toString, Function.identity()));

        Box.Nullable<TimeUnit> leftSide = Box.Nullable.ofNull();
        Box.Nullable<String> rightSide = Box.Nullable.ofNull();
        right.acceptBoth(leftSide, rightSide, TimeUnit.HOURS, "wahoo");
        assertEquals(TimeUnit.HOURS, leftSide.get());
        assertEquals("word", rightSide.get());
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
        assertEquals("A", forValue.get());
        forValue.set("B");
        assertEquals("B", forValue.get());
    }

    @Test
    public void testInstance() {

        Box<String> forValue = Box.from(this, DurianTestCase::getValue, DurianTestCase::setValue);
        forValue.set("A");
        assertEquals("A", forValue.get());
        forValue.set("B");
        assertEquals("B", forValue.get());
    }

    @Test
    public void testa() throws Exception {
        Consumer<String> consumer = s -> {};
        new StringPrinter(consumer);
    }
}
