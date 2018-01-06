package fm.mox.spikes.functionaljava.scalacheck.sut;

import lombok.Getter;

/**
 * Counter, aka System Under Test
 * @author mmoci (mmoci at expedia dot com).
 */
@Getter
public class Counter {
    private int n;
    public Counter(int n) {
        this.n = n;
    }
    public Integer increment() {
        this.n = n + 1;
        return this.n;
    }
}
