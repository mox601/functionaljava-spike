package fm.mox.spikes.functionaljava.state.account;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class StateMachine<I, S> {

    /**
     * flipped StateTuple a,s to s,a to match fjava order
     * replace Either, List, Optional, Function from fj
     *
     */

   /* F<I, State<S, Unit>> function;

    public StateMachine(List<Tuple<Condition<I, S>, Transition<I, S>>> transitions) {
        function = value -> StateMonad.transition(state ->
        {
            StateTuple<S, I> stateTuple = new StateTuple<>(state, value);
            Option<StateTuple<S, I>> optStateTuple = Option.fromNull(stateTuple);
            Function<StateTuple<S, I>, Optional<S>> stateTupleToOptional = (StateTuple<S, I> st) ->
            {
                Option<Tuple<Condition<I, S>, Transition<I, S>>> first = transitions
                        .filter((Tuple<Condition<I, S>, Transition<I, S>> x) -> x._1.test(st))
                        .headOption();

                Function<Tuple<Condition<I, S>, Transition<I, S>>, S> tupleToS = (Tuple<Condition<I, S>, Transition<I, S>> y) -> y._2.apply(st);

                return first.map(tupleToS);
            };
            return optStateTuple.flatMap(stateTupleToOptional).get();
        });
    }

    public State<S, S> process(List<I> inputs) {
        List<State<S, Unit>> a = inputs.map(function);
        //different fold
        State<S, List<Nothing>> b = State.sequence(a);
        return b.flatMap(x -> State.init());
    }
    */
}
