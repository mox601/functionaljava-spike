package fm.mox.spikes;

import fj.P;
import fj.P2;
import fj.control.Trampoline;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.util.stream.LongStream;

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.valueOf;
import static org.testng.Assert.assertEquals;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class FibTest {

    @Test
    public void name() {
//        Fib f = new IterativeFib();
//        Fib f = new StreamsFib();
        Fib f = new TrampolineFib();

        assertEquals(f.fib(0), valueOf(1));
        assertEquals(f.fib(1), valueOf(1));
        assertEquals(f.fib(2), valueOf(2));
        assertEquals(f.fib(3), valueOf(3));
        assertEquals(f.fib(4), valueOf(5));
        assertEquals(f.fib(5), valueOf(8));
        assertEquals(f.fib(10000), new BigInteger("54438373113565281338734260993750380135389184554695967026247715841208582865622349017083051547938960541173822675978026317384359584751116241439174702642959169925586334117906063048089793531476108466259072759367899150677960088306597966641965824937721800381441158841042480997984696487375337180028163763317781927941101369262750979509800713596718023814710669912644214775254478587674568963808002962265133111359929762726679441400101575800043510777465935805362502461707918059226414679005690752321895868142367849593880756423483754386342639635970733756260098962462668746112041739819404875062443709868654315626847186195620146126642232711815040367018825205314845875817193533529827837800351902529239517836689467661917953884712441028463935449484614450778762529520961887597272889220768537396475869543159172434537193611263743926337313005896167248051737986306368115003088396749587102619524631352447499505204198305187168321623283859794627245919771454628218399695789223798912199431775469705216131081096559950638297261253848242007897109054754028438149611930465061866170122983288964352733750792786069444761853525144421077928045979904561298129423809156055033032338919609162236698759922782923191896688017718575555520994653320128446502371153715141749290913104897203455577507196645425232862022019506091483585223882711016708433051169942115775151255510251655931888164048344129557038825477521111577395780115868397072602565614824956460538700280331311861485399805397031555727529693399586079850381581446276433858828529535803424850845426446471681531001533180479567436396815653326152509571127480411928196022148849148284389124178520174507305538928717857923509417743383331506898239354421988805429332440371194867215543576548565499134519271098919802665184564927827827212957649240235507595558205647569365394873317659000206373126570643509709482649710038733517477713403319028105575667931789470024118803094604034362953471997461392274791549730356412633074230824051999996101549784667340458326852960388301120765629245998136251652347093963049734046445106365304163630823669242257761468288461791843224793434406079917883360676846711185597501", 10));
    }

    private interface Fib  {
        BigInteger fib(long position);
    }

    private static class IterativeFib implements Fib {

        @Override
        public BigInteger fib(long position) {
            BigInteger current = ONE;
            BigInteger next = ONE;
            if (position < 2) return ONE;
            for (int i = 2; i <= position; i++) {
                BigInteger temp = current.add(next);
                current = next;
                next = temp;
            }
            return next;
        }
    }

    private static class StreamsFib implements Fib {
        @Override
        public BigInteger fib(long position) {
            return LongStream.range(0L, position)
                    .boxed()
                    .map(ignore -> P.p(ZERO, ZERO))
                    .reduce(P.p(ZERO, ONE), (t, ignore) -> P.p(t._2(), t._1().add(t._2())))
                    ._2();
        }
    }

    private static class TrampolineFib implements Fib {
        @Override
        public BigInteger fib(long position) {
            return internalFib(P.p(ZERO, ONE), position).run();
        }

        private Trampoline<BigInteger> internalFib(P2<BigInteger, BigInteger> currentAndNext, long position) {
            Trampoline<BigInteger> t = null;
            if (position == 0L) {
                t = Trampoline.pure(ONE);
            } else if (position == 1L) {
                BigInteger sum = currentAndNext._1().add(currentAndNext._2());
                t = Trampoline.pure(sum);
            } else {
                t = Trampoline.suspend(
                        //lazy tuple so that recursion does not blow the stack
                        P.lazy(unit -> {
                            BigInteger next = currentAndNext._2();
                            BigInteger sum = currentAndNext._1().add(currentAndNext._2());
                            return internalFib(P.p(next, sum), position - 1);
                        }));
            }
            return t;
        }
    }
}
