package fm.mox.spikes.functionaljava.state.accountweb;

import java.util.function.Function;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface Transition<I, S> extends Function<StateTuple<S, I>, S> {
}