package fm.mox.spikes.rxjava;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.flowables.GroupedFlowable;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class GroupByTestCase {

    @Test
    public void testName() throws InterruptedException {
        Flowable<GroupedFlowable<String, Integer>> groupedBy =
                Flowable.fromIterable(getList(1000))
                        .groupBy((i) -> 0 == i % 2 ? "EVEN" : "ODD");

        Disposable subscribe = groupedBy
                .subscribe((group) -> {
                    log.info("Key " + group.getKey());
                    group.subscribe((x) -> log.info((group.getKey() + ": " + x)));
                });
        Thread.sleep(1_000L);
    }

    static List<Integer> getList(int length) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(i);
        }
        return list;
    }
}
