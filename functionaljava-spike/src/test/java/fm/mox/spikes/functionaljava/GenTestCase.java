package fm.mox.spikes.functionaljava;

import fj.F;
import fj.F2;
import fj.P;
import fj.P1;
import fj.P2;
import fj.Try;
import fj.data.List;
import fj.data.Option;
import fj.data.Validation;
import fj.function.Try0;
import fj.test.Arbitrary;
import fj.test.CheckResult;
import fj.test.Gen;
import fj.test.Property;
import fj.test.Rand;
import fj.test.Shrink;
import fm.mox.spikes.functionaljava.scalacheck.Actions;
import fm.mox.spikes.functionaljava.scalacheck.Command;
import fm.mox.spikes.functionaljava.scalacheck.Commands;
import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Slf4j
public class GenTestCase {

    static final Shrink<User> USER_SHRINK = shrinkP2(shrinkString, shrinkString).map(p2 -> new User(p2._1(), p2._2()), u -> p(u.getUsername(), u.getPassword()));

    static final Gen<String> ARB_STRING_BOUNDARIES = arbList(arbCharacterBoundaries).map(List::asString);

    static final Gen<User> ARB_USER = ARB_STRING_BOUNDARIES
        .filter((String s) -> s.length() > 0 && s.length() < 10)
        .bind(arbString.filter(p -> p.length() > 2 && p.length() < 10), username -> password -> new User(username, password));


    @Test
    public void givenGeneratedListAllItemsAreLtEq100() throws Exception {
        final F<List<Integer>, P1<Property>> allLtEq =
            integers -> p(prop(integers.forall(integer -> integer <= 100)));
        final Property forall = forall(listOf(choose(0, 100)), shrinkList(shrinkInteger), allLtEq);
        assertTrue(forall.check(0, 10).isPassed());
    }

    @Test
    public void testFPScala() throws Exception {
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
    public void testArb() throws Exception {

    }

    //  TODO  "I would generate arbitrary functions and then map over that to produce
    // arbitrary states (using the existing State class)"

    //TODO how to test a FSM?
    /* state, transitions, ... */

    @Test
    public void testElevatorFsm() throws Exception {
        final F2<Elevator, Elevator.Button, Elevator> elevatorPressButton = Elevator::press;

        assertEquals(elevatorPressButton.f(new Elevator(), DOWN).floor, GROUND);
        assertEquals(elevatorPressButton.f(new Elevator(), UP).floor, FIRST);

        assertEquals(elevatorPressButton.f(new Elevator(FIRST), DOWN).floor, GROUND);
//        assertEquals(elevatorPressButton.f(new Elevator(FIRST), UP).floor, FIRST);

        //generator of sequences of Buttons
        Gen<Elevator.Button> buttons = Gen.elements(Elevator.Button.DOWN, Elevator.Button.UP);

        Gen<List<Elevator.Button>> listOfButtons = Gen.listOf(buttons);

        int amount = 100;

        for (int i = 0; i < amount; i++) {
            List<Elevator.Button> gen = listOfButtons.gen(i, Rand.standard);
            log.info(gen + "");
        }

        Shrink<Elevator.Button> buttonShrink = Shrink.shrinkBoolean.map(b -> b ? DOWN : UP, button -> button.equals(DOWN));

        F<List<Elevator.Button>, P1<Property>> propWithResults = pressedButtons -> {

            Elevator elevator = new Elevator();

            for (final Elevator.Button button : pressedButtons) {
                elevator = elevator.press(button);
            }

            Elevator.Floor floor = elevator.getFloor();

            boolean isFirstOrGround = floor.equals(FIRST) || floor.equals(GROUND);

            return p(prop(isFirstOrGround));
        };

        final Property aProperty = forall(listOfButtons, shrinkList(buttonShrink), propWithResults);

        final CheckResult check = aProperty.check(0, 10);

        assertTrue(check.isPassed());
    }

    @Value
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
    public void testName() throws Exception {
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


    //    implement this http://www.scalacheck.org/files/scaladays2014/index.html#12
    //    https://github.com/rickynils/scalacheck/blob/master/src/main/scala/org/scalacheck/commands/Commands.scala
    @Test
    public void testCounter() throws Exception {

    }

    //in test code
    public static class CounterCommands implements Commands<Counter, Long> {
        @Override
        public Counter newSut(Long state) {
            return new Counter(state.intValue());
        }
        @Override
        public void destroySut(Counter counter) {
        }

        @Override
        public boolean initialPreCondition(Long state) {
            return true;
        }

        @Override
        public Gen<Long> genInitialState() {
            return Arbitrary.arbLong;
        }
        @Override
        public Gen<Command> genCommand(Long state) {
            Gen<Command> incrGen = null;
            Gen<Command> getGen = null;
            List<Gen<Command>> commands = List.list(incrGen, getGen);
            return Gen.oneOf(commands);
        }


        /** [[Actions]] generator */
        /*private def actions(threadCount: Int, maxParComb: Int): Gen[Actions] = {
    import Gen.{const, listOfN, sized}

            def sizedCmds(s: State)(sz: Int): Gen[(State,Commands)] = {
                val l: List[Unit] = List.fill(sz)(())
                l.foldLeft(const((s,Nil:Commands))) { case (g,()) =>
                    for {
                    (s0,cs) <- g
                    c <- genCommand(s0) suchThat (_.preCondition(s0))
                } yield (c.nextState(s0), cs :+ c)
                }
            }*/

        Gen<Actions> actions() {

            Gen<Long> s0 = genInitialState();

            Gen<Actions> g = null;
            g.filter(new F<Actions, Boolean>() {
                         @Override
                         public Boolean f(Actions as) {

                             /*
                             as.parCmds.length != 1
                             && as.parCmds.forall(_.nonEmpty)
                             && initialPreCondition(as.s)
                             && (cmdsPrecond(as.s, as.seqCmds) match {
                                 case (s,true) => as.parCmds.forall(cmdsPrecond(s,_)._2)
                                 case _ => false
                             */

                             boolean b1 = as.getParCmds().length() != 1;
                             boolean forall = as.getParCmds().forall(List::isNotEmpty);
                             boolean b = initialPreCondition(as.getS());

                             P2<Long, Boolean> p = cmdsPrecond(as.getS(), as.getSeqCmds());

                             if (p._2()) {
                                 as.getParCmds().forall(new F<List<Command>, Boolean>() {
                                     @Override
                                     public Boolean f(List<Command> commands) {
                                         return cmdsPrecond(as.getS(), as.getSeqCmds())._2();
                                     }
                                 });
                             }

                             return null;
                         }
                     }
            );

            return null;
        }

        private P2<Long, Boolean> cmdsPrecond(Long s, List<Command> seqCmds) {
            return null;
        }

        public static Property property() {

            //val suts = collection.mutable.Map.empty[AnyRef,(State,Option[Sut])]
            Map<Object, P2<Long, Option<Counter>>> suts = new HashMap<>();

            /*
            val sutId = suts.synchronized {
            val initSuts = for((state,None) <- suts.values) yield state
            val runningSuts = for((_,Some(sut)) <- suts.values) yield sut
            if (canCreateNewSut(as.s, initSuts, runningSuts)) {
                    val sutId = new AnyRef
                    suts += (sutId -> (as.s -> None))
                    Some(sutId)
                } else None
            }
            */

            //synchronized
            Collection<P2<Long, Option<Counter>>> values = suts.values();

            Option<Object> optSutId = sutId(suts, values);

            if (optSutId.isSome()) {
                //Some

                /*val sut = newSut(as.s)
                def removeSut():Unit = {
                        suts.synchronized {
                    suts -= id
                    destroySut(sut)
                }*/




            } else {
                //None
            }


            //verify all properties
            Increment increment = new Increment();
            Get get = new Get();
            return null;
        }

        private static Option<Object> sutId(Map<Object, P2<Long, Option<Counter>>> suts,
                                            Collection<P2<Long, Option<Counter>>> values) {
            Object sutId = null;
            // valuesWithNoneCounter
            java.util.List<Long> initSuts = values
                    .stream()
                    .filter(longOptionP2 -> longOptionP2._2().isNone())
                    .map(P2::_1)
                    .collect(Collectors.toList());

            java.util.List<Counter> runningSuts = values
                    .stream()
                    .filter(longOptionP2 -> longOptionP2._2().isSome())
                    .map(P2::_2)
                    .map(counters -> counters.some())
                    .collect(Collectors.toList());

            if (canCreateNewSut(null, initSuts, runningSuts)) {
                P2<Long, Option<Counter>> tuple = P.p(null, Option.none());
                sutId = new Object();
                suts.put(sutId, tuple);
            }

            return Option.fromNull(sutId);
        }

        // If you want to allow only one [[Sut]] instance to exist at any given time
        //   *  (a singleton [[Sut]]), implement this method the following way:
        static boolean canCreateNewSut(Long state, Collection<Long> initSuts, Collection<Counter> runningSuts) {
            return initSuts.isEmpty() && runningSuts.isEmpty();
        }
    }

    // Counter, aka System Under Test
    @Getter
    public static class Counter {
        private int n;
        public Counter(int n) {
            this.n = n;
        }
        public Integer increment() {
            this.n = n + 1;
            return this.n;
        }
    }

    @Value
    public static class Increment implements Command<Counter, Long, Integer> {
        @Override
        public Integer run(Counter sut) {
            return sut.increment();
        }
        @Override
        public Long nextState(Long previous) {
            return previous + 1;
        }
        @Override
        public boolean preCondition(Long state) {
            return true;
        }
        @Override
        public Property postCondition(Long state, Try0<Integer, Exception> result) {
            P1<Validation<Exception, Integer>> f = Try.f(result);
            Validation<Exception, Integer> integers = f._1();
            Property resultEqualToStateProp = Property.prop(integers.success().equals(state.intValue()));
            return Property.prop(integers.isSuccess()).and(resultEqualToStateProp);
        }
    }

    @Value
    public static class Get implements Command<Counter, Long, Integer> {
        @Override
        public Integer run(Counter sut) {
            return sut.getN();
        }
        @Override
        public Long nextState(Long previous) {
            return previous;
        }
        @Override
        public boolean preCondition(Long state) {
            return true;
        }
        @Override
        public Property postCondition(Long state, Try0<Integer, Exception> result) {
            //TODO
            return null;
        }
    }
}
