package fm.mox.spikes.functionaljava.state;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Optional;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class Memo extends HashMap<BigInteger, BigInteger> {

    public Optional<BigInteger> retrieve(BigInteger key) {
        return Optional.ofNullable(super.get(key));
    }

    public Memo addEntry(BigInteger key, BigInteger value) {
        super.put(key, value);
        return this;
    }
}
