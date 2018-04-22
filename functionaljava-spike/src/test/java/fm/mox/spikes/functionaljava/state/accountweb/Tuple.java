package fm.mox.spikes.functionaljava.state.accountweb;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class Tuple<A, B> {

    public final A _1;
    public final B _2;

    public Tuple(A a, B b) {
        _1 = a;
        _2 = b;
    }
}