package fm.mox.spikes.scalacheck;

import fj.data.List;
import lombok.Value;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Value
public class Actions<SUT, STATE, RESULT> {
    STATE s;
    List<Command<SUT, STATE, RESULT>> seqCmds;
    List<List<Command<SUT, STATE, RESULT>>> parCmds;
}
