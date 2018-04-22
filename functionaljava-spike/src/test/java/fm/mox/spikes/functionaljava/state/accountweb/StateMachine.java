package fm.mox.spikes.functionaljava.state.accountweb;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class StateMachine<I, S> {

    /**
     * flipped StateTuple a,s to s,a to match fjava order
     * use either from fj
     *
     */


    Function<I, StateMonad<S, Nothing>> function;

    public StateMachine(List<Tuple<Condition<I, S>, Transition<I, S>>> transitions) {
        function = value -> StateMonad.transition(state ->
        {
            StateTuple<S, I> stateTuple = new StateTuple<>(state, value);
            Optional<StateTuple<S, I>> optStateTuple = Optional.of(stateTuple);
            Function<StateTuple<S, I>, Optional<S>> stateTupleToOptional = (StateTuple<S, I> st) ->
            {
                Optional<Tuple<Condition<I, S>, Transition<I, S>>> first = transitions
                        .filter((Tuple<Condition<I, S>, Transition<I, S>> x) -> x._1.test(st))
                        .findFirst();

                Function<Tuple<Condition<I, S>, Transition<I, S>>, S> tupleToS = (Tuple<Condition<I, S>, Transition<I, S>> y) -> y._2.apply(st);

                return first.map(tupleToS);
            };
            return optStateTuple.flatMap(stateTupleToOptional).get();
        });
    }

    public StateMonad<S, S> process(List<I> inputs) {
        List<StateMonad<S, Nothing>> a = inputs.map(function);
        StateMonad<S, List<Nothing>> b = StateMonad.compose(a);
        return b.flatMap(x -> StateMonad.get());
    }
}
