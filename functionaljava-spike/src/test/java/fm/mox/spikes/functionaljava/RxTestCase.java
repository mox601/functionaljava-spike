package fm.mox.spikes.functionaljava;

import io.reactivex.Single;
import io.reactivex.functions.Function;
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
}
