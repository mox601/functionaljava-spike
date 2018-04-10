package fm.mox.spikes.scalacheck.commands;

import fj.P1;
import fj.Try;
import fj.data.Validation;
import fj.function.Try0;
import fj.test.Property;
import fm.mox.spikes.scalacheck.Command;
import fm.mox.spikes.scalacheck.sut.Counter;

/**
 * @author mmoci (mmoci at expedia dot com).
 */
public class Increment implements Command<Counter, Long, Integer> {
    @Override
    public Integer run(Counter sut) {
        return sut.increment();
    }

    @Override
    public Long nextState(Long previous) {
        return previous + 1;
    }

    @Override
    public boolean preCondition(Long state) {
        return true;
    }

    @Override
    public Property postCondition(Long state, Try0<Integer, Exception> result) {
        P1<Validation<Exception, Integer>> f = Try.f(result);
        Validation<Exception, Integer> integers = f._1();
        Property resultEqualToStateProp = Property.prop(integers.success().equals(state.intValue()));
        return Property.prop(integers.isSuccess()).and(resultEqualToStateProp);
    }
}
