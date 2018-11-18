package fm.mox.spikes.functionaljava;

import fj.F;
import fj.P;
import fj.P2;
import fj.Unit;
import fj.control.parallel.Actor;
import fj.control.parallel.Promise;
import fj.control.parallel.Strategy;
import fj.data.List;
import fj.function.Integers;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
@Slf4j
public class PromiseTestCase {

    private static int CUTOFF = 15;

    @Test(enabled = false)
    public void fib() throws Exception {

        final int threads = Runtime.getRuntime().availableProcessors();
        final ExecutorService pool = Executors.newFixedThreadPool(threads);
        final Strategy<Unit> su = Strategy.executorStrategy(pool);
        final Strategy<Promise<Integer>> spi = Strategy.executorStrategy(pool);

        // This actor performs output and detects the termination condition.
        final Actor<List<Integer>> out = Actor.actor(su, fs -> {
            for (final P2<Integer, Integer> p : fs.zipIndex()) {
                log.info(MessageFormat.format("n={0} => {1}", p._2(), p._1()));
            }
            pool.shutdown();
        });

        // A parallel recursive Fibonacci function
        final F<Integer, Promise<Integer>> fib = new F<Integer, Promise<Integer>>() {
            @Override
            public Promise<Integer> f(final Integer n) {
                return n < CUTOFF ? Promise.promise(su, P.p(seqFib(n))) : f(n - 1).bind(f(n - 2), Integers.add);
            }
        };

        log.info("Calculating Fibonacci sequence in parallel...");
        Promise.join(su, spi.parMap(fib, List.range(0, 46)).map(Promise.sequence(su))).to(out);
        Thread.sleep(1_000L);
    }

    public static int seqFib(final int n) {
        return n < 2 ? n : seqFib(n - 1) + seqFib(n - 2);
    }
}
