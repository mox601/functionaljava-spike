package fm.mox.spikes.functionaljava.state;

import lombok.Value;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Value
public class VendingMachine {
    protected boolean locked;
    protected int items;
    protected int coins;
    VendingMachine next(final Input i) {
        if (items == 0) {
            return this;
        } else if (i == Input.COIN && !locked) {
            return this;
        } else if (i == Input.TURN && locked) {
            return this;
        } else if (i == Input.COIN && locked) {
            return new VendingMachine(false, items, coins + 1);
        } else if (i == Input.TURN && !locked) {
            return new VendingMachine(true, items - 1, coins);
        } else {
            return this;
        }
    }
}
