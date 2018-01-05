package fm.mox.spikes.functionaljava.scalacheck.commands;

import fj.function.Try0;
import fj.test.Property;
import fm.mox.spikes.functionaljava.scalacheck.Command;
import fm.mox.spikes.functionaljava.scalacheck.sut.Counter;

/**
 * @author mmoci (mmoci at expedia dot com).
 */
public class Get implements Command<Counter, Long, Integer> {
    @Override
    public Integer run(Counter sut) {
        return sut.getN();
    }
    @Override
    public Long nextState(Long previous) {
        return previous;
    }
    @Override
    public boolean preCondition(Long state) {
        return true;
    }
    @Override
    public Property postCondition(Long state, Try0<Integer, Exception> result) {
        //TODO
        return null;
    }
}
