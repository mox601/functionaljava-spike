package fm.mox.spikes.functionaljava.state.accountweb;

import java.util.function.Function;

import fj.Unit;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class StateMonad<S, A> {

    public final Function<S, StateTuple<S, A>> runState;

    public StateMonad(Function<S, StateTuple<S, A>> runState) {
        this.runState = runState;
    }

    public static <S, A> StateMonad<S, A> unit(A a) {
        return new StateMonad<>(s -> new StateTuple<>(s, a));
    }

    public static <S> StateMonad<S, S> get() {
        return new StateMonad<>(s -> new StateTuple<>(s, s));
    }

    //getState allows creating a StateMonad from a function S -> A.
    public static <S, A> StateMonad<S, A> getState(Function<S, A> f) {
        return new StateMonad<>(s -> new StateTuple<>(s, f.apply(s)));
    }

    public static <S> StateMonad<S, Unit> transition(Function<S, S> f) {
        return transition(f, Unit.unit());
        //return new StateMonad<>(s -> new StateTuple<>(Nothing.instance, f.apply(s)));
    }

    //transition takes a function from state to state and a value
    // and returns a new StateMonad holding the value and the state
    // resulting from the application of the function.
    // In other words, it allows changing the state without changing the value.
    public static <S, A> StateMonad<S, A> transition(Function<S, S> f, A value) {
        return new StateMonad<>(s -> new StateTuple<>(f.apply(s), value));
    }

    /**
     *
     * Switched to foldLeft to change order.
     *
     * @param fs
     * @param <S>
     * @param <A>
     * @return
     */
    public static <S, A> StateMonad<S, List<A>> compose(List<StateMonad<S, A>> fs) {
        return fs.foldRight(
                StateMonad.unit(List.empty()),
                f -> acc -> f.map2(acc, a -> b -> b.cons(a)));
    }

    public <B> StateMonad<S, B> flatMap(Function<A, StateMonad<S, B>> f) {
        return new StateMonad<>(s -> {
            StateTuple<S, A> temp = runState.apply(s);
            return f.apply(temp.value).runState.apply(temp.state);
        });
    }

    public <B> StateMonad<S, B> map(Function<A, B> f) {
        return flatMap(a -> StateMonad.unit(f.apply(a)));
    }

    public <B, C> StateMonad<S, C> map2(StateMonad<S, B> sb, Function<A, Function<B, C>> f) {
        return flatMap(a -> sb.map(b -> f.apply(a).apply(b)));
    }

    //eval allows easy retrieval of the value hold by the StateMonad.
    public A eval(S s) {
        return runState.apply(s).value;
    }

}
