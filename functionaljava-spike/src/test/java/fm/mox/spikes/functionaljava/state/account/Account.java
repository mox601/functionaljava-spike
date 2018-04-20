package fm.mox.spikes.functionaljava.state.account;

import fj.F;
import fj.P;
import fj.P2;
import fj.data.Either;
import fj.data.List;
import fj.data.State;
import fm.mox.spikes.functionaljava.state.account.commands.Command;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class Account {

    public static State<Integer, List<Either<Exception, Integer>>> createMachine(List<Command> inputs) {

        Condition<Command, Outcome> predicate1 = t -> t._1().isDeposit();
        Transition<Command, Outcome> transition1 = t ->
                new Outcome(t._2().account + t._1().getAmount(),
                t._2().operations.cons(Either.right(t._1().getAmount())));

        Condition<Command, Outcome> predicate2 = t -> t._1().isWithdraw() && t._2().account >= t._1().getAmount();
        Transition<Command, Outcome> transition2 = t ->
                new Outcome(t._2().account - t._1().getAmount(),
                        t._2().operations.cons(Either.right(-t._1().getAmount())));

        Condition<Command, Outcome> predicate3 = t -> true;
        Transition<Command, Outcome> transition3 = t ->
                new Outcome(t._2().account,
                t._2().operations.cons(Either.left(new IllegalStateException(String.format("Can't withdraw %s because balance is only %s", t._1().getAmount(), t._2().account)))));

        List<P2<Condition<Command, Outcome>, Transition<Command, Outcome>>> possibleTransitions = List.list(
                P.p(predicate1, transition1),
                P.p(predicate2, transition2),
                P.p(predicate3, transition3));

            //<S, A, B> State<S, List<B>> traverse(List<A> list, F<A, State<S, B>> f) {

            return State.traverse(inputs, new F<Command, State<Integer, Either<Exception, Integer>>>() {
            @Override
            public State<Integer, Either<Exception, Integer>> f(Command input) {

//                possibleTransitions.filter();

                return null;
            }
        });
    }


}
