package fm.mox.spikes.functionaljava;

import cyclops.stream.ReactiveSeq;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class CyclopsReactTestCase {

    //https://github.com/aol/cyclops-react
    @Test
    public void testName() throws Exception {

        Stream<Integer> stream = ReactiveSeq.range(0, 1000)
                .map(i -> i * 2);

        stream.forEach(System.out::println);
        List<Integer> replayed = stream.collect(Collectors.toList());
        stream.map(i -> "hello  " + i).forEach(log::info);
    }
}
