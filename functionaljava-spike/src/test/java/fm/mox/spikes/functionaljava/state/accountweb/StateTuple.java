package fm.mox.spikes.functionaljava.state.accountweb;

import java.util.Objects;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class StateTuple<S, A> {

    public final S state;
    public final A value;

    public StateTuple(S state, A value) {
        this.state = Objects.requireNonNull(state);
        this.value = Objects.requireNonNull(value);
    }
}