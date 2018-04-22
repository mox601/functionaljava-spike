package fm.mox.spikes.functionaljava.state.accountweb;

import java.util.function.Predicate;

/**
 * flipped statetuple to s,i
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface Condition<I, S> extends Predicate<StateTuple<S, I>> {}