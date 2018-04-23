package fm.mox.spikes.functionaljava.state.accountweb;

import java.util.Optional;
import java.util.function.Function;

import fj.P2;
import fj.Unit;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class StateMachine<I, S> {

    /**
     * flipped StateTuple a,s to s,a to match fjava order
     * replace Either, List, Optional, Function from fj
     * use unit instead of Nothing
     * use P2 instead of Tuple
     */

    Function<I, StateMonad<S, Unit>> function;

    public StateMachine(List<P2<Condition<I, S>, Transition<I, S>>> transitions) {
        function = value -> StateMonad.transition(state ->
        {
            StateTuple<S, I> stateTuple = new StateTuple<>(state, value);
            Optional<StateTuple<S, I>> optStateTuple = Optional.of(stateTuple);
            Function<StateTuple<S, I>, Optional<S>> stateTupleToOptional = (StateTuple<S, I> st) ->
            {
                Optional<P2<Condition<I, S>, Transition<I, S>>> firstApplicableTransition = transitions
                        .filter((P2<Condition<I, S>, Transition<I, S>> x) -> x._1().test(st))
                        .findFirst();

                Function<P2<Condition<I, S>, Transition<I, S>>, S> tupleToS = (P2<Condition<I, S>, Transition<I, S>> y) -> y._2().apply(st);

                return firstApplicableTransition.map(tupleToS);
            };
            return optStateTuple.flatMap(stateTupleToOptional).get();
        });
    }

    public StateMonad<S, S> process(List<I> inputs) {
        List<StateMonad<S, Unit>> a = inputs.map(function);
        StateMonad<S, List<Unit>> b = StateMonad.compose(a);
        return b.flatMap(x -> StateMonad.get());
    }
}
