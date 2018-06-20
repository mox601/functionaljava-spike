package fm.mox.spikes.rxjava;

import io.reactivex.functions.Function;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public enum HashSetSupplier implements Callable<Set<Object>>, Function<Object, Set<Object>> {
    INSTANCE;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Callable<Set<T>> asCallable() {
        return (Callable) INSTANCE;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, O> Function<O, Set<T>> asFunction() {
        return (Function) INSTANCE;
    }

    @Override
    public Set<Object> call() throws Exception {
        return new HashSet<>();
    }

    @Override
    public Set<Object> apply(Object o) throws Exception {
        return new HashSet<>();
    }
}
