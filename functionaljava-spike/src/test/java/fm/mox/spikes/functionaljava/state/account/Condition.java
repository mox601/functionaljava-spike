package fm.mox.spikes.functionaljava.state.account;

import fj.P2;

import java.util.function.Predicate;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface Condition<I, S> extends Predicate<P2<I, S>> {}
