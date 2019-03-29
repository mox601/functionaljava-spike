package fm.mox.spikes.functionaljava.state;

import fj.F2;
import fj.data.List;
import fj.data.State;
import fj.test.Gen;
import io.vavr.test.Arbitrary;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class VendingMachineTest {

    @Test
    public void testVending() throws Exception {

        final State<VendingMachine, VendingMachine> init = State.init();
        final State<VendingMachine, VendingMachine> afterOneCoin =
                init.map(vendingMachine -> vendingMachine.next(Input.COIN));

        VendingMachine s  = new VendingMachine(true, 10, 0);
        VendingMachine s1 = new VendingMachine(true, 10, 0);

        assertEquals(afterOneCoin.run(s)._1(), s1);
//        assertEquals(afterOneCoin.run(s)._2(), s1);
    }

    // https://github.com/functionaljava/functionaljava/blob/master/demo/src/main/java/fj/demo/StateDemo_VendingMachine.java
    @Test
    public void testVendingMachineEval() throws Exception {
        final VendingMachine vendingAfterFive = vendingMachineStateAfterInput().eval(new VendingMachine(true, 5, 0));
        log.info(vendingAfterFive.toString());
        final VendingMachine oracle = new VendingMachine(true, 3, 2);
        log.info("m1: {}, oracle: {}, equals: {}", vendingAfterFive, oracle, vendingAfterFive.equals(oracle));
    }

    @Test
    public void testVendingMachineRun() throws Exception {
        log.info(vendingMachineStateAfterInput().run(new VendingMachine(true, 5, 0)).toString());
    }

    private static State<VendingMachine, VendingMachine> vendingMachineStateAfterInput() {
        return simulate(List.list(
                Input.COIN, Input.TURN,
                Input.TURN, Input.COIN,
                Input.COIN, Input.TURN));
    }

    static State<VendingMachine, VendingMachine> simulate(final List<Input> list) {
        final State<VendingMachine, VendingMachine> beginningValue = State.init();
        F2<State<VendingMachine, VendingMachine>, Input, State<VendingMachine, VendingMachine>> fToApplyEach
                = (vendingMachineState, input) -> vendingMachineState.map(m -> m.next(input));
        return list.foldLeft(fToApplyEach, beginningValue);
    }

    // https://blog.johanneslink.net/2018/09/06/stateful-testing/

    ////////////////////////////////////////

    public interface Action<M> {

        default boolean precondition(M model) {
            return true;
        }

        M run(M model);
    }

    public static class InsertCoin implements Action<VendingMachine> {
        @Override
        public VendingMachine run(VendingMachine model) {
            int coinsBefore = model.coins;
            VendingMachine next = model.next(Input.COIN);
            int coinsAfter = next.coins;

            assertNotEquals(coinsBefore, 0);
            assertEquals(coinsAfter, coinsBefore + 1);

            return next;
        }
    }

    public static class Turn implements Action<VendingMachine> {
        @Override
        public VendingMachine run(VendingMachine model) {
            VendingMachine next = model.next(Input.TURN);

            //empty

            return next;
        }
    }

    static Gen<Action<VendingMachine>> actions() {
        return Gen.elements(insertCoin(), turn());
    }

    private static Action<VendingMachine> insertCoin() {
        return new InsertCoin();
    }

    private static Action<VendingMachine> turn() {
        return new Turn();
    }

    @Test
    public void testStateMachine() {

    }

}
