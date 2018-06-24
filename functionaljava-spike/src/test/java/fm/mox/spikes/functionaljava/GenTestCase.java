package fm.mox.spikes.functionaljava;

import static fj.P.p;
import static fj.test.Arbitrary.arbCharacterBoundaries;
import static fj.test.Arbitrary.arbList;
import static fj.test.Arbitrary.arbString;
import static fj.test.Gen.choose;
import static fj.test.Gen.listOf;
import static fj.test.Property.forall;
import static fj.test.Property.prop;
import static fj.test.Shrink.shrinkInteger;
import static fj.test.Shrink.shrinkList;
import static fj.test.Shrink.shrinkP2;
import static fj.test.Shrink.shrinkString;
import static fm.mox.spikes.functionaljava.GenTestCase.Elevator.Button.DOWN;
import static fm.mox.spikes.functionaljava.GenTestCase.Elevator.Button.UP;
import static fm.mox.spikes.functionaljava.GenTestCase.Elevator.Floor.FIRST;
import static fm.mox.spikes.functionaljava.GenTestCase.Elevator.Floor.GROUND;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import fj.F;
import fj.F2;
import fj.P1;
import fj.data.List;
import fj.data.Option;
import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;
import fj.test.Rand;
import fj.test.Shrink;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Slf4j
public class GenTestCase {

    private static final Shrink<User> USER_SHRINK = shrinkP2(shrinkString, shrinkString).map(p2 -> new User(p2._1(), p2._2()), u -> p(u.getUsername(), u.getPassword()));

    private static final Gen<String> ARB_STRING_BOUNDARIES = arbList(arbCharacterBoundaries).map(List::asString);

    private static final Gen<User> ARB_USER = ARB_STRING_BOUNDARIES
            .filter((String s) -> s.length() > 0 && s.length() < 10)
            .bind(arbString.filter(p -> p.length() > 2 && p.length() < 10), username -> password -> new User(username, password));


    @Test
    public void givenGeneratedListAllItemsAreLtEq100() {
        final F<List<Integer>, P1<Property>> allLtEq =
                integers -> p(prop(integers.forall(integer -> integer <= 100)));
        final Property forall = forall(listOf(choose(0, 100)), shrinkList(shrinkInteger), allLtEq);
        assertTrue(forall.check(0, 10).isPassed());
    }

    @Test
    public void testFPScala() {
        final F<List<Integer>, P1<Property>> reversedTwiceEqualsOriginal =
                integers -> p(prop(integers.reverse().reverse().equals(integers)));
        final F<List<Integer>, P1<Property>> headEqualsLastOfReverse =
                integers -> {
                    boolean isHeadEqualsToLastOfReverse = true;
                    final Option<Integer> headOpt = integers.headOption();
                    if (headOpt.isSome()) {
                        final Integer head = headOpt.some();
                        final Integer lastOfReverse = integers.reverse().last();
                        isHeadEqualsToLastOfReverse = head.equals(lastOfReverse);
                    }
                    return p(prop(isHeadEqualsToLastOfReverse));
                };
        final Gen<Integer> choose = choose(0, 10);
        final Property reversedTwiceProp = forall(listOf(choose), shrinkList(shrinkInteger), reversedTwiceEqualsOriginal);
        final Property oppositeEqualsProp = forall(listOf(choose), shrinkList(shrinkInteger), headEqualsLastOfReverse);
        final CheckResult check = reversedTwiceProp.and(oppositeEqualsProp).check(0, 10);
        assertTrue(check.isPassed());
    }

    @Test
    public void testArb() {

    }

    //  TODO  "I would generate arbitrary functions and then map over that to produce
    // arbitrary states (using the existing State class)"

    //TODO how to test a FSM?
    /* state, transitions, ... */

    @Test
    public void testElevatorFsm() {
        final F2<Elevator, Elevator.Button, Elevator> elevatorPressButton = Elevator::press;

        assertEquals(elevatorPressButton.f(new Elevator(), DOWN).floor, GROUND);
        assertEquals(elevatorPressButton.f(new Elevator(), UP).floor, FIRST);

        assertEquals(elevatorPressButton.f(new Elevator(FIRST), DOWN).floor, GROUND);
//        assertEquals(elevatorPressButton.f(new Elevator(FIRST), UP).floor, FIRST);

        //generator of sequences of Buttons
        Gen<Elevator.Button> buttons = Gen.elements(Elevator.Button.DOWN, Elevator.Button.UP);

        Gen<List<Elevator.Button>> listOfButtons = Gen.listOf(buttons);

        Shrink<Elevator.Button> buttonShrink = Shrink.shrinkBoolean.map(b -> b ? DOWN : UP, button -> button.equals(DOWN));

        F<List<Elevator.Button>, P1<Property>> propWithResults = pressedButtons -> {

            Elevator elevator = new Elevator();

            for (final Elevator.Button button : pressedButtons) {
                elevator = elevator.press(button);
            }

            Elevator.Floor floor = elevator.floor;

            boolean isFirstOrGround = floor.equals(FIRST) || floor.equals(GROUND);

            return p(prop(isFirstOrGround));
        };

        final Property aProperty = forall(listOfButtons, shrinkList(buttonShrink), propWithResults);

        final CheckResult check = aProperty.check(0, 10);

        assertTrue(check.isPassed());
    }

    public static class Elevator {

        Floor floor;

        private Elevator() {
            this(GROUND);
        }

        private Elevator(final Floor floor) {
            this.floor = floor;
        }

        public Elevator press(final Button button) {
            Elevator afterPushing = this;
            switch (this.floor) {
                case GROUND:
                    if (button.equals(UP)) {
                        afterPushing = new Elevator(FIRST);
                    }
                    break;
                case FIRST:
                    if (button.equals(DOWN)) {
                        afterPushing = new Elevator(GROUND);
                    }
                    // introduced a bug here:use a property checker to discover it
                    if (button.equals(UP)) {
                        afterPushing = new Elevator(GROUND);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("floor is not correct '" + this.floor + "'");
            }
            return afterPushing;
        }

        public enum Floor {
            GROUND, FIRST
        }

        public enum Button {
            UP, DOWN
        }
    }

    @Test
    public void testName() {
        final Property oppositeEqualsProp = forall(ARB_USER, USER_SHRINK, user -> p(prop(user.count() < 10)));
        final CheckResult check = oppositeEqualsProp.check(10, 50, 4, 10);
        assertTrue(check.isPassed());
    }

    @Value
    public static class User {
        String username;
        String password;

        public int count() {
            return this.username.length();
        }
    }

}
