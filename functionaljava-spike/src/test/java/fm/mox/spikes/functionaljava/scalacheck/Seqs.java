package fm.mox.spikes.functionaljava.scalacheck;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public final class Seqs {

    private Seqs() {
    }

    // Number of sequences to test (n = threadCount, m = parSz):
    //   2^m * 3^m * ... * n^m
    //
    // def f(n: Long, m: Long): Long =
    //   if(n == 1) 1
    //   else math.round(math.pow(n,m)) * f(n-1,m)
    //
    //    m  1     2       3         4           5
    // n 1   1     1       1         1           1
    //   2   2     4       8        16          32
    //   3   6    36     216      1296        7776
    //   4  24   576   13824    331776     7962624
    //   5 120 14400 1728000 207360000 24883200000

    public static Long seqs(Long n, Long m) {
        if (n == 1) {
            return 1L;
        } else {
            return Math.round(Math.pow(n.doubleValue(), m.doubleValue())) * seqs(n - 1, m);
        }
    }

    public static Integer parSz(Integer threadCount, Integer maxParComb) {
        if (threadCount < 2) {
            return 0;
        } else {
            Integer parSz = 1;
            while (seqs(threadCount.longValue(), parSz.longValue()) < maxParComb) {
                parSz += 1;
            }
            return parSz;
        }
    }
}
