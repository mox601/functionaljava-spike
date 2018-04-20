package fm.mox.spikes.functionaljava.state.account.commands;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface Command {
    boolean isDeposit();
    boolean isWithdraw();
    int getAmount();
}
