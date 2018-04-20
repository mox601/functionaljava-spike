package fm.mox.spikes.functionaljava.state.account;

import fj.P2;
import fj.data.Either;
import fj.data.List;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class Outcome extends P2<Integer, List<Either<Exception, Integer>>> {

    public final Integer account;
    public final List<Either<Exception, Integer>> operations;

    public Outcome(Integer account, List<Either<Exception, Integer>> operations) {
        super();
        this.account = account;
        this.operations = operations;
    }

    @Override
    public Integer _1() {
        return account;
    }

    @Override
    public List<Either<Exception, Integer>> _2() {
        return operations;
    }
}
