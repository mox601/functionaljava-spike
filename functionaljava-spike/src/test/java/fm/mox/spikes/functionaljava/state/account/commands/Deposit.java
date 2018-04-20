package fm.mox.spikes.functionaljava.state.account.commands;

import lombok.Getter;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Getter
public class Deposit implements Command {

    private final int amount;

    public Deposit(int amount) {
        super();
        this.amount = amount;
    }

    @Override
    public boolean isDeposit() {
        return true;
    }

    @Override
    public boolean isWithdraw() {
        return false;
    }
}