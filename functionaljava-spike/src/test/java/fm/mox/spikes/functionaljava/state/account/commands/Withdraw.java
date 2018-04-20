package fm.mox.spikes.functionaljava.state.account.commands;

import lombok.Getter;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Getter
public class Withdraw implements Command {

    private final int amount;

    public Withdraw(int amount) {
        super();
        this.amount = amount;
    }

    @Override
    public boolean isDeposit() {
        return false;
    }

    @Override
    public boolean isWithdraw() {
        return true;
    }
}
