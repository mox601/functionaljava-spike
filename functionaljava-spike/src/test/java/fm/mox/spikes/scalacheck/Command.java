package fm.mox.spikes.scalacheck;

import fj.F;
import fj.P2;
import fj.function.Try0;
import fj.test.Property;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface Command<SUT, STATE, RESULT> {
    RESULT run(SUT sut);
    STATE nextState(STATE previous);
    boolean preCondition(STATE state);
    Property postCondition(STATE state, Try0<RESULT, Exception> result);

    default P2<Try0<String, Exception>, F<STATE, Property>> runPC(SUT sut) {
        //TODO

        /*
        *
        * import Prop.BooleanOperators
      val r = Try(run(sut))
      (r.map(_.toString), s => preCondition(s) ==> postCondition(s,r))

        * */

        return null;
    }
}
