package fm.mox.spikes.functionaljava.state.account;

import fj.P2;

import java.util.function.Function;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface Transition<I, S> extends Function<P2<I, S>, S> {}
