package fm.mox.spikes.functionaljava.state.accountweb;


import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class List<T> {

    private java.util.List<T> list = new ArrayList<>();

    public static <T> List<T> empty() {
        return new List<T>();
    }

    @SafeVarargs
    public static <T> List<T> apply(T... ta) {
        List<T> result = new List<>();
        for (T t : ta)
            result.list.add(t);
        return result;
    }

    public List<T> cons(T t) {
        List<T> result = new List<>();
        result.list.add(t);
        result.list.addAll(list);
        return result;
    }

    public <U> U foldRight(U seed, Function<T, Function<U, U>> f) {
        U result = seed;
        for (int i = list.size() - 1; i >= 0; i--) {
            result = f.apply(list.get(i)).apply(result);
        }
        return result;
    }

    public <U> U foldLeft(U seed, Function<T, Function<U, U>> f) {
        U result = seed;
        for (int i = 0; i < list.size(); i++) {
            result = f.apply(list.get(i)).apply(result);
        }
        return result;
    }

    public <U> List<U> map(Function<T, U> f) {
        List<U> result = new List<>();
        for (T t : list) {
            result.list.add(f.apply(t));
        }
        return result;
    }

    public List<T> filter(Function<T, Boolean> f) {
        List<T> result = new List<>();
        for (T t : list) {
            if (f.apply(t)) {
                result.list.add(t);
            }
        }
        return result;
    }

    public Optional<T> findFirst() {
        return list.size() == 0
                ? Optional.empty()
                : Optional.ofNullable(list.get(0));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("[");
        String separator = "";
        for (T t : list) {
            s.append(separator).append(t);
            separator = ", ";
        }
        return s.append("]").toString();
    }
}
