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
     * replace Either, List, Optional, Function, Unit, P2 from fj
     */

    Function<I, StateMonad<S, Unit>> function;

    public StateMachine(List<P2<Condition<I, S>, Transition<I, S>>> transitions) {
        function = value -> StateMonad.transition(state ->
        {
            StateTuple<S, I> stateTuple = new StateTuple<>(state, value);
            Optional<StateTuple<S, I>> optStateTuple = Optional.of(stateTuple);
            Function<StateTuple<S, I>, Optional<S>> stateTupleToOptional = (StateTuple<S, I> st) ->
            {
                Optional<P2<Condition<I, S>, Transition<I, S>>> firstApplicableTransition =
                        transitions
                                .filter((P2<Condition<I, S>, Transition<I, S>> x) -> x._1().test(st))
                                .findFirst();

                Function<P2<Condition<I, S>, Transition<I, S>>, S> tupleToS = (P2<Condition<I, S>, Transition<I, S>> y) -> y._2().apply(st);

                return firstApplicableTransition.map(tupleToS);
            };
            return optStateTuple.flatMap(stateTupleToOptional).get();
        });
    }


    /*
    by ivano pagano
    *
It will help you to revisit the definition of a State[S, A] which has the shape of a function

type State[S,A] = S => (A, S)

which in our exmaple means

Machine => ((Int, Int), Machine)

that is: you give it the starting (bootstrap) value of the Machine,
and the function gives you back the final Machine state,
tupled with the result value (candy, coins)

To be more exact, the book creates a class wrapping the function so we have

case class State[S,A]( run: S => (A,S) )

where the function "run" has the above signature Machine => ((Int, Int), Machine)

Executing the machine means providing the needed inputs to your simulation and call "run"

val startMachine: Machine = ??? //choose an initial state
val inputs: List[Input] = ??? // decide what operations are applied to the machine

//run it
val finalState: ((Int, Int), Machine) = simulateMachine(inputs).run(startMachine)

you can pattern match to extract the tuple elements like

val ((candies, coins), finalMachine) : ((Int, Int), Machine) = simulateMachine(inputs).run(startMachine)

The simulateMachine function is only describing a sequence of mutations
on a starting machine state based on inputs you give.
To have results, you need to actually pass specific inputs and then run the State
(which will thread all the modifications from one state to the next for you)

    * */
    public StateMonad<S, S> process(List<I> inputs) {
        List<StateMonad<S, Unit>> a = inputs.map(function);
        StateMonad<S, List<Unit>> b = StateMonad.compose(a);
        return b.flatMap(x -> StateMonad.get());
    }
}
