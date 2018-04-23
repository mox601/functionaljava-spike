package fm.mox.spikes.functionaljava.state.accountweb;

import fj.P;
import fj.P2;
import fj.data.Either;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class Account {

    public static StateMachine<Input, Outcome> createMachine() {

        Condition<Input, Outcome> canDeposit = stateTuple -> stateTuple.value.isDeposit();
        Transition<Input, Outcome> deposit = stateTuple -> new Outcome(stateTuple.state.account + stateTuple.value.getAmount(), stateTuple.state.operations.cons(Either.right(stateTuple.value.getAmount())));

        Condition<Input, Outcome> canWithdraw = stateTuple -> stateTuple.value.isWithdraw() && stateTuple.state.account >= stateTuple.value.getAmount();
        Transition<Input, Outcome> withdraw = stateTuple -> new Outcome(stateTuple.state.account - stateTuple.value.getAmount(), stateTuple.state.operations.cons(Either.right(- stateTuple.value.getAmount())));

        Condition<Input, Outcome> canFallback = stateTuple -> true;
        Transition<Input, Outcome> fallback = stateTuple -> new Outcome(stateTuple.state.account, stateTuple.state.operations.cons(Either.left(new IllegalStateException(String.format("Can't withdraw %s because balance is only %s", stateTuple.value.getAmount(), stateTuple.state.account)))));

        List<P2<Condition<Input, Outcome>, Transition<Input, Outcome>>> transitions = List.apply(
                P.p(canDeposit, deposit),
                P.p(canWithdraw, withdraw),
                P.p(canFallback, fallback));

        return new StateMachine<>(transitions);
    }
}