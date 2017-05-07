package fm.mox.spikes.functionaljava;

import fj.F;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.State;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.helpers.AbsoluteTimeDateFormat;
import org.testng.annotations.Test;

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

    @Test
    public void testName() throws Exception {
        final State<Object, String> value = State.constant("value");
        assertEquals(value.eval(null), "value");
        assertEquals(value.eval(1), "value");
        assertEquals(value.eval(1L), "value");
        assertEquals(value.eval(new AbsoluteTimeDateFormat()), "value");
    }

    @Test
    public void testUnit() throws Exception {
        assertEquals(State.unit(o -> P.p(o, "value")).eval(null), "value");
    }

    // https://github.com/functionaljava/functionaljava/blob/master/demo/src/main/java/fj/demo/StateDemo_Greeter.java
    @Test
    public void testGreeter() throws Exception {
        final State<String, String> init = State.init();
        final State<String, String> st1 = init.flatMap(s -> {
            return State.unit(s1 -> {
                return P.p("Batman", "Hello " + s1);
            });
        });
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
}
