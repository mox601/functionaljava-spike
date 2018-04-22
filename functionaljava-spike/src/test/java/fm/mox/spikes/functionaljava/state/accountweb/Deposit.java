package fm.mox.spikes.functionaljava.state.accountweb;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class Deposit implements Input {

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

    @Override
    public int getAmount() {
        return this.amount;
    }
}
