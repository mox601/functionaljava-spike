package fm.mox.spikes.functionaljava;

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
        final State<Object, String> value = State.unit(o -> P.p(o, "value"));
        final String eval = value.eval(null);
        assertEquals(eval, "value");
    }

    // https://github.com/functionaljava/functionaljava/blob/master/demo/src/main/java/fj/demo/StateDemo_Greeter.java
    @Test
    public void testGreeter() throws Exception {
        final State<String, String> st1 = State.<String>init().flatMap(s -> State.unit(s1 -> P.p("Batman", "Hello " + s1)));
        final P2<String, String> robin = st1.run("Robin");
        assertEquals(robin._1(), "Batman");
        assertEquals(robin._2(), "Hello Robin");
    }

    // https://github.com/functionaljava/functionaljava/blob/master/demo/src/main/java/fj/demo/StateDemo_VendingMachine.java
    @Test
    public void testVendingMachine() throws Exception {
        final State<VendingMachine, VendingMachine> stateAfterSomeCoins = simulate(State.init(), List.list(COIN,
                TURN, TURN, COIN, COIN, TURN));
        final VendingMachine lockedWithFiveThingsNoCoins = new VendingMachine(true, 5, 0);
        final VendingMachine vendingAfterFive = stateAfterSomeCoins.eval(lockedWithFiveThingsNoCoins);
        log.info(vendingAfterFive.toString());

        final VendingMachine lockedWithFiveItemsNoCoins = new VendingMachine(true, 5, 0);
        final P2<VendingMachine, VendingMachine> vendingMachineTuple = stateAfterSomeCoins.run(lockedWithFiveItemsNoCoins);
        log.info(vendingMachineTuple.toString());

        final VendingMachine oracle = new VendingMachine(true, 3, 2);
        log.info("m1: %s, oracle: %s, equals: %b", vendingAfterFive, oracle, vendingAfterFive.equals(oracle));
    }

    static State<VendingMachine, VendingMachine> simulate(final State<VendingMachine, VendingMachine> beginningValue,
                                                          final List<Input> list) {
        return list.foldLeft((vendingMachineState, input) -> vendingMachineState.map(m -> m.next(input)), beginningValue);
    }

    public enum Input {COIN, TURN}

    @Value
    public static class VendingMachine {

        private final boolean locked;
        private final int items;
        private final int coins;

        VendingMachine next(Input i) {

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

    public enum Transition {SUCCESS, FAIL_UNDER_THRESHOLD, FAIL_THRESHOLD_REACHED, RESET_TIMEOUT, FAIL, RAISE_CIRCUIT_OPEN}

    public enum Status {CLOSED, OPEN, HALF_OPEN}

    @Value
    public static class Circuit {

        private final Status status;

        Circuit next(final Transition input) {

            Circuit circuitInNewState = this;

            if (status.equals(Status.CLOSED) && input.equals(Transition.SUCCESS)) {
                circuitInNewState = this;
            } else if (status.equals(Status.CLOSED) && input.equals(Transition.FAIL_UNDER_THRESHOLD)) {
                circuitInNewState = this;
            } else if (status.equals(Status.CLOSED) && input.equals(Transition.FAIL_THRESHOLD_REACHED)) {
                // return open
                circuitInNewState = new Circuit(Status.OPEN);
            } else if (status.equals(Status.OPEN) && input.equals(Transition.RAISE_CIRCUIT_OPEN)) {
                circuitInNewState = this;
            } else if (status.equals(Status.OPEN) && input.equals(Transition.RESET_TIMEOUT)) {
                // return halfopen
                circuitInNewState = new Circuit(Status.HALF_OPEN);
            } else if (status.equals(Status.HALF_OPEN) && input.equals(Transition.FAIL)) {
                // return open
                circuitInNewState = new Circuit(Status.OPEN);
            } else if (status.equals(Status.HALF_OPEN) && input.equals(Transition.SUCCESS)) {
                // return closed
                circuitInNewState = new Circuit(Status.CLOSED);
            }

            return circuitInNewState;
        }
    }
}
