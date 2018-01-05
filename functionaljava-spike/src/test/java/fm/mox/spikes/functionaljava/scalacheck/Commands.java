package fm.mox.spikes.functionaljava.scalacheck;

import fj.test.Gen;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface Commands<SUT, STATE> {
    SUT newSut(STATE state);
    void destroySut(SUT counter);
    boolean initialPreCondition(STATE state);
    Gen<Long> genInitialState();
    Gen<Command> genCommand(STATE state);
}
