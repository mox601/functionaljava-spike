package fm.mox.spikes.functionaljava;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class RxTestCase {

    @Test
    public void testName() throws Exception {
        Flowable<String> hw = Flowable.just("Hello world");
        Flowable<String> concatenatedResponse = getStringFlowable(hw);
        assertEquals(concatenatedResponse.blockingSingle(), "Hello world 1");
    }

    private Flowable<String> getStringFlowable(Flowable<String> input) {
        Flowable<String> fetched = input
                .flatMap((Function<String, Publisher<String>>) s -> {
                    //fetch from repository by input
                    return fetchById(input);
                });
        Flowable<Integer> inputRank = fetched.map(this::fetchRankById);
        return inputRank.zipWith(input, (integer, s) -> s + " " + integer);
    }

    private Flowable<String> fetchById(Flowable<String> input) {
        return Flowable.just("this");
    }

    private Integer fetchRankById(String input) {
        return 1;
    }
}
