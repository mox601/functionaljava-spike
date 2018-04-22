package fm.mox.spikes.functionaljava.state.accountweb;

import fj.data.Either;

/**
 * The last utility class is Outcome, which represents the result returned by the state machine.
 *
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class Outcome {

    public final Integer account;
    public final List<Either<Exception, Integer>> operations;

    public Outcome(Integer account, List<Either<Exception, Integer>> operations) {
        super();
        this.account = account;
        this.operations = operations;
    }

    public String toString() {
        return "(" + account.toString() + "," + operations.toString() + ")";
    }
}
