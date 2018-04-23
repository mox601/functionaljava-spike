package fm.mox.spikes.functionaljava.state.accountweb;

import org.testng.annotations.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class AccTest {

    @Test
    public void testName() {
        List<Input> inputs = List.apply(
                new Deposit(100),
                new Withdraw(50),
                new Withdraw(150),
                new Deposit(200),
                new Withdraw(150));

        StateMonad<Outcome, Outcome> state = Account.createMachine().process(inputs);
        Outcome outcome = state.eval(new Outcome(0, List.empty()));

        //it shows in the opposite way
        log.info(outcome.toString());
    }
}
