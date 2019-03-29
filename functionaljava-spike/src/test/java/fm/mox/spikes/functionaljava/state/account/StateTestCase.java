package fm.mox.spikes.functionaljava.state.account;

import fj.data.Either;
import fj.data.List;
import fj.data.State;
import fm.mox.spikes.functionaljava.state.account.commands.Command;
import fm.mox.spikes.functionaljava.state.account.commands.Deposit;
import fm.mox.spikes.functionaljava.state.account.commands.Withdraw;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class StateTestCase {

    //TODO convert to functionaljava
    @Test
    public void testDomain() throws Exception {

        List<Command> commands = List.list(
                new Deposit(100),
                new Withdraw(50),
                new Withdraw(150),
                new Deposit(200),
                new Withdraw(150));

        State<Integer, List<Either<Exception, Integer>>> state = Account.createMachine(commands);

        // Again, nothing has been evaluated yet.
        // To get the result, we just evaluate the result, using an initial state:

        //List<Either<Exception, Integer>> outcome = state.eval(0);

        //log.info(outcome.toString());

    }
}
