package fm.mox.spikes.functionaljava.state.accountweb;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public interface Input {
    boolean isDeposit();
    boolean isWithdraw();
    int getAmount();
}
