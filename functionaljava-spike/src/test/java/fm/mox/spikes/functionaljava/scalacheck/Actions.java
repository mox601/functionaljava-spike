package fm.mox.spikes.functionaljava.scalacheck;

import fj.data.List;
import lombok.Value;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Value
public class Actions {
    Long s;
    List<Command> seqCmds;
    List<List<Command>> parCmds;
}
