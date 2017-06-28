package fm.mox.spikes.functionaljava;

import fj.F;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.State;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.util.function.Function;

import static fm.mox.spikes.functionaljava.StateTestCase.Input.COIN;
import static fm.mox.spikes.functionaljava.StateTestCase.Input.TURN;
import static org.testng.Assert.assertEquals;

/**
 * take examples from https://github.com/functionaljava/functionaljava/tree/master/demo/src/main/java/fj/demo
 *
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Slf4j
public class StateTestCase {


    //State monad is just an abstraction for a function
    // that takes a state and returns an intermediate value and some new state value.


    /*The flatMap method takes a function from A (a value) to StateMonad<S, B>
        and return a new StateMonad<S, B>. (In our case, A is the same as B.)
        The new type contains the new value and the new state that result
        from the application of the function.*/

    //        This class holds a function from a state to a tuple (value, state)

    /*
    * -- look at our counter and return "foo" or "bar"
-- along with the incremented counter:
fromStoAandS :: Int -> (String,Int)
fromStoAandS c | c `mod` 5 == 0 = ("foo",c+1)
               | otherwise = ("bar",c+1)
    * */

    //    http://brandon.si/code/the-state-monad-a-tutorial-for-the-confused/
    @Test
    public void testNamea() throws Exception {

        //state, pair(written state, output)
        F<Integer, P2<Integer, String>> fromStoAandS = c -> {
            P2<Integer, String> result;
            if (c % 5 == 0) {
                result = P.p(c + 1, "foo");
            } else {
                result = P.p(c + 1, "bar");
            }
            return result;
        };

        State<Integer, String> stateIntString = State.unit(fromStoAandS);

        P2<Integer, String> afterRun = stateIntString.run(1);

        String resultStatus = stateIntString.eval(1);
        Integer resultOutput = stateIntString.exec(1);

        log.info(resultStatus);
        log.info(resultOutput + "");
        log.info(afterRun + "");

    }

    @Test
    public void testName() throws Exception {
        final State<Object, String> value = State.constant("value");
        assertEquals(value.eval(null), "value");
        assertEquals(value.eval(1), "value");
        assertEquals(value.eval(1L), "value");
        byte[] bytes = new byte[0];
        assertEquals(value.eval(new ByteArrayInputStream(bytes)), "value");
    }

    @Test
    public void testUnit() throws Exception {
        assertEquals(State.unit(o -> P.p(o, "value")).eval(null), "value");
    }

    /*
    * http://www.smartjava.org/content/scalaz-features-everyday-usage-part-3-state-monad-writer-monad-and-lenses
    With the (Reader monad) we already saw how you could inject some context into a function. That context, however, wasn't changeable. With the state monad, we're provided with a nice pattern we can use to pass a mutable context around in a safe and pure manner.*/

//    https://github.com/Muzietto/BeckmanStateMonad/blob/master/test/net/faustinelli/monad/state/beckman/MonadicLabeling.java

    // https://github.com/functionaljava/functionaljava/blob/master/demo/src/main/java/fj/demo/StateDemo_Greeter.java
    @Test
    public void testGreeter() throws Exception {
        final State<String, String> init = State.init();
        final State<String, String> st1 =
                init.flatMap(s -> State.unit(s1 -> P.p("Batman", "Hello " + s1)));
        final P2<String, String> robin = st1.run("Robin");

        assertEquals(robin, P.p("Batman", "Hello Robin"));
    }

    @Test
    public void testVending() throws Exception {

        final State<VendingMachine, VendingMachine> init = State.init();
        final State<VendingMachine, VendingMachine> afterOneCoin =
                init.map(vendingMachine -> vendingMachine.next(COIN));

        VendingMachine s = new VendingMachine(true, 10, 0);
        VendingMachine s1 = new VendingMachine(true, 10, 0);

        assertEquals(afterOneCoin.run(s)._1(), s1);
//        assertEquals(afterOneCoin.run(s)._2(), s1);
    }

    private static State<VendingMachine, VendingMachine> vendingMachineStateAfterInput() {
        return simulate(List.list(COIN, TURN, TURN, COIN, COIN, TURN));
    }

    static State<VendingMachine, VendingMachine> simulate(final List<Input> list) {
        final State<VendingMachine, VendingMachine> beginningValue = State.init();
        return list.foldLeft((vendingMachineState, input) -> vendingMachineState.map(m -> m.next(input)), beginningValue);
    }

    // https://github.com/functionaljava/functionaljava/blob/master/demo/src/main/java/fj/demo/StateDemo_VendingMachine.java
    @Test
    public void testVendingMachineEval() throws Exception {
        final VendingMachine vendingAfterFive = vendingMachineStateAfterInput().eval(new VendingMachine(true, 5, 0));
        log.info(vendingAfterFive.toString());
        final VendingMachine oracle = new VendingMachine(true, 3, 2);
        log.info("m1: %s, oracle: %s, equals: %b", vendingAfterFive, oracle, vendingAfterFive.equals(oracle));
    }

    @Test
    public void testVendingMachineRun() throws Exception {
        log.info(vendingMachineStateAfterInput().run(new VendingMachine(true, 5, 0)).toString());
    }

    public enum Input {COIN, TURN}

    @Value
    public static class VendingMachine {

        private final boolean locked;
        private final int items;
        private final int coins;

        VendingMachine next(final Input i) {
            if (items == 0) {
                return this;
            } else if (i == COIN && !locked) {
                return this;
            } else if (i == TURN && locked) {
                return this;
            } else if (i == COIN && locked) {
                return new VendingMachine(false, items, coins + 1);
            } else if (i == TURN && !locked) {
                return new VendingMachine(true, items - 1, coins);
            } else {
                return this;
            }
        }
    }


    //    http://www.smartjava.org/content/scalaz-features-everyday-usage-part-3-state-monad-writer-monad-and-lenses
    @Test
    public void testGetFromState() throws Exception {

        P2<LeftOver, Integer> run = addToState(20)
                .flatMap(i -> getFromState(5))
                .flatMap(i -> getFromState(5))
                .flatMap(i -> getFromState(5))
//                .withs(leftOver -> new LeftOver(9000))
//                .flatMap(i -> getFromState(10))
                .run(new LeftOver(10));

        log.info(run + "");
    }

    @Value
    private static class LeftOver {
        private final Integer size;
    }

    State<LeftOver, Integer> getFromState(Integer a) {
        return State.unit(x -> P.p(new LeftOver(x.size - a), a));
    }

    State<LeftOver, Integer> addToState(Integer a) {
        return State.unit(x -> P.p(new LeftOver(x.size + a), a));
    }
}
