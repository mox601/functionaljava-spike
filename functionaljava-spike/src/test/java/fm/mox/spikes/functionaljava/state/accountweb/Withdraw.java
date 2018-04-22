package fm.mox.spikes.functionaljava.state.accountweb;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class Withdraw implements Input {

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

    @Override
    public int getAmount() {
        return this.amount;
    }
}
