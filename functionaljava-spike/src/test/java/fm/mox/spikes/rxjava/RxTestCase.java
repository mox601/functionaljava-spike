package fm.mox.spikes.rxjava;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.reactivestreams.Publisher;
import org.testng.annotations.Test;

import cyclops.companion.Streams;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.functions.Functions;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import lombok.extern.slf4j.Slf4j;

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
                .flatMap((Function<List<String>, Single<String>>) aList -> Single.just(Streams.join( aList.stream(), " ")))
                .doOnEvent((s, throwable) -> log.info(s));
        assertEquals(spaceSeparated.blockingGet(), "one two");
    }

    @Test
    public void testParallelSubscription() throws Exception {
        Single<String> stringSingle = Single.fromCallable(() -> {
            log.info("sleeping");
            silentlySleep(1_000L);
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
        silentlySleep(4_000L);
    }

    private void subscribe(Flowable<String> aSingle) {
        aSingle.subscribeOn(Schedulers.io()).subscribe(log::info, Throwable::printStackTrace, () -> log.info("onComplete"));
    }

    @Test
    public void testPagination() {
        PublishSubject<String> objectPublishSubject = PublishSubject.create();

        //TODO configure backpressure
        Flowable<String> items = objectPublishSubject.toFlowable(BackpressureStrategy.DROP);

        AtomicBoolean shouldThrow = new AtomicBoolean(false);

        Flowable<Boolean> buffer = items
                .observeOn(Schedulers.io())
                .doOnNext(item -> log.info("item " + item))
                //.buffer(100L, TimeUnit.MILLISECONDS, Schedulers.io(), 2)
                //buffer removing duplicates
                .buffer(100L, TimeUnit.MILLISECONDS, Schedulers.io(), 3, HashSetSupplier.asCallable(), false)
                .filter(page -> !page.isEmpty())
                .doOnNext(strings -> log.info("buffer " + strings.toString()))
                .flatMap((Function<Set<String>, Publisher<String>>) Flowable::fromIterable)
                .buffer(100L, TimeUnit.MILLISECONDS, Schedulers.io(), 2)
                .filter(page -> !page.isEmpty())
                .doOnNext(strings -> log.info("re-buffer " + strings.toString()))
                .map(strings -> publish(strings, shouldThrow));

        //TODO handle publishing errors, logging and resuming next
        Disposable subscribe = buffer.subscribe(
            b -> log.info("published? " + b),
            Functions.ON_ERROR_MISSING,
            Functions.EMPTY_ACTION);

        //publish from different threads
        twoThreadsPublishSleeping(objectPublishSubject, 10L, shouldThrow);

        silentlySleep(1_000L);
    }

    private void twoThreadsPublishSleeping(PublishSubject<String> stringPublishSubject,
                                           long millis, AtomicBoolean shouldThrow) {
        Thread one = new Thread(() -> {
            stringPublishSubject.onNext("1");
            silentlySleep(millis);
            stringPublishSubject.onNext("2");
            silentlySleep(millis);
            stringPublishSubject.onNext("3");
            silentlySleep(millis);
        });

        Thread two = new Thread(() -> {
            stringPublishSubject.onNext("1");
            silentlySleep(millis);
            stringPublishSubject.onNext("4");
            silentlySleep(millis);
            shouldThrow.set(true);
            stringPublishSubject.onNext("5");
            silentlySleep(millis);
        });

        one.start();
        two.start();
    }

    private static void silentlySleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            //nop
        }
    }

    private Boolean publish(List<String> aList, AtomicBoolean shouldThrow) {
        log.info("publishing " + aList);
        silentlySleep(200L);
        String theString = "published " + aList;
        if (shouldThrow.get()) {
            //throw instead of returning boolean?
            throw new RuntimeException("exception while publishing " + aList.toString());
        }
        return true;
    }
}
