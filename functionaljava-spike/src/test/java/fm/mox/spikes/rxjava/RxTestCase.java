package fm.mox.spikes.rxjava;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class RxTestCase {

    @Test
    public void testSingle() throws Exception {
        Single<String> spaceSeparated = Single
                .concat(Arrays.asList(Single.just("one"), Single.just("two")))
                .doOnNext(m -> log.info(m + ""))
                .toList()
                .flatMap((Function<List<String>, Single<String>>) aList -> {
                    StringBuilder sb = new StringBuilder();
                    String aSeparator = "";
                    for (String anItem : aList) {
                        sb.append(aSeparator).append(anItem);
                        aSeparator = " ";
                    }
                    return Single.just(sb.toString());
                })
                .doOnEvent((s, throwable) -> log.info(s));
        assertEquals(spaceSeparated.blockingGet(), "one two");
    }

    @Test
    public void testParallelSubscription() throws Exception {
        Single<String> stringSingle = Single.fromCallable(() -> {
            log.info("sleeping");
            Thread.sleep(1000L);
            log.info("returning");
            return "a";
        });
        Single<String> aSingle = stringSingle.map(s -> {
            log.info("mapped " + s);
            return s;
        });
        Flowable<String> share = aSingle.toFlowable().share();
        for (int i = 0; i < 3; i++) {
            new Thread(() -> subscribe(share)).start();
        }
        Thread.sleep(4000L);
    }

    private void subscribe(Flowable<String> aSingle) {
        aSingle.subscribeOn(Schedulers.io()).subscribe(log::info, Throwable::printStackTrace, () -> log.info("onComplete"));
    }
}
