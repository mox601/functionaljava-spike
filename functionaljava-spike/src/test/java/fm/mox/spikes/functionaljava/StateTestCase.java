package fm.mox.spikes.functionaljava;

import fj.F;
import fj.P;
import fj.P2;
import fj.data.List;
import fj.data.State;
import org.testng.annotations.Test;

import static fm.mox.spikes.functionaljava.StateTestCase.Input.COIN;
import static fm.mox.spikes.functionaljava.StateTestCase.Input.TURN;
import static org.testng.Assert.assertEquals;

/**
 * take examples from https://github.com/functionaljava/functionaljava/tree/master/demo/src/main/java/fj/demo
 *
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class StateTestCase {

    @Test
    public void testName() throws Exception {

        final State<Object, String> value = State.constant("value");
        final String eval = value.eval(null);
        assertEquals(eval, "value");

    }

    @Test
    public void testUnit() throws Exception {

        final State<Object, String> value = State.unit(new F<Object, P2<Object, String>>() {
            @Override
            public P2<Object, String> f(final Object o) {

                //TUPLE
                return new P2<Object, String>() {
                    @Override
                    public Object _1() {

                        return o;
                    }

                    @Override
                    public String _2() {

                        return "value";
                    }
                };
            }
        });
        final String eval = value.eval(null);
        assertEquals(eval, "value");

    }

    // https://github.com/functionaljava/functionaljava/blob/master/demo/src/main/java/fj/demo/StateDemo_Greeter.java
    @Test
    public void testGreeter() throws Exception {

        final State<String, String> st1 = State.<String>init().flatMap(
                new F<String, State<String, String>>() {
                    @Override
                    public State<String, String> f(String s) {

                        return State.unit(new F<String, P2<String, String>>() {
                            @Override
                            public P2<String, String> f(String s) {

                                return P.p("Batman", "Hello " + s);
                            }
                        });
                    }
                });
        assertEquals(st1.run("Robin")._1(), "Batman");
        assertEquals(st1.run("Robin")._2(), "Hello Robin");

    }

    // https://github.com/functionaljava/functionaljava/blob/master/demo/src/main/java/fj/demo/StateDemo_VendingMachine.java
    @Test
    public void testVendingMachine() throws Exception {

        final State<VendingMachine, VendingMachine> stateAfterSomeCoins = simulate(List.list(COIN,
                TURN, TURN, COIN, COIN, TURN));
        final VendingMachine vendingFive = new VendingMachine(true, 5, 0);
        final VendingMachine vendingAfterFive = stateAfterSomeCoins.eval(vendingFive);
        System.out.println(vendingAfterFive);

        final VendingMachine s1 = new VendingMachine(true, 5, 0);
        final P2<VendingMachine, VendingMachine> vendingMachineTuple = stateAfterSomeCoins.run(s1);
        System.out.println(vendingMachineTuple);

        final VendingMachine oracle = new VendingMachine(true, 3, 2);
        System.out.printf("m1: %s, oracle: %s, equals: %b", vendingAfterFive, oracle,
                vendingAfterFive.equals(oracle));

    }

    static State<VendingMachine, VendingMachine> simulate(List<Input> list) {

        return list.foldLeft((vendingMachineVendingMachineState, input) -> {

            return vendingMachineVendingMachineState.map(new F<VendingMachine, VendingMachine>() {
                @Override
                public VendingMachine f(VendingMachine m) {

                    return m.next(input);
                }
            });
        }, State.<VendingMachine>init());
    }

    public enum Input {COIN, TURN}

    public static class VendingMachine {

        private boolean locked;
        private int items;
        private int coins;

        public VendingMachine(boolean lock, int things, int numCoins) {

            locked = lock;
            items = things;
            coins = numCoins;
        }

        /**
         * Equals generated by Intellij
         */
        @Override
        public boolean equals(Object o) {

            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            VendingMachine that = (VendingMachine) o;

            if (coins != that.coins) {
                return false;
            }
            if (items != that.items) {
                return false;
            }
            if (locked != that.locked) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {

            int result = (locked ? 1 : 0);
            result = 31 * result + items;
            result = 31 * result + coins;
            return result;
        }

        public String toString() {

            return String.format("VendingMachine(locked=%b,items=%d,coins=%d)", locked, items,
                    coins);
        }

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

}
