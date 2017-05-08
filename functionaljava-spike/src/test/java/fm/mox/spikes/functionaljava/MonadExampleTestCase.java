package fm.mox.spikes.functionaljava;

import fj.P;
import fj.P2;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
public class MonadExampleTestCase {

    // http://blog.sigfpe.com/2006/08/you-could-have-invented-monads-and.html
    @Test
    public void test() throws Exception {

        //any f
        Function<Float, Float> anF = aFloat -> aFloat / 8.0F;
        Function<Float, Float> aG = aFloat -> aFloat / 12.0F;

        //"debuggable" functions
        Function<Float, P2<Float, String>> f = aFloat -> P.p(aFloat / 3.0F, "divided by 3");
        Function<Float, P2<Float, String>> g = aFloat -> P.p(aFloat / 4.0F, "divided by 4");
        Function<Float, P2<Float, String>> unit = aFloat -> P.p(aFloat, "");

        BindOnString<Float> bindOnString = new BindOnString<>();

        //
        Function<P2<Float, String>, P2<Float, String>> bindF = bind(f);
        Function<P2<Float, String>, P2<Float, String>> bindG = bind(g);
        Function<P2<Float, String>, P2<Float, String>> bindUnit = bind(unit);

        // We can now compose them together to make a new debuggable function (bind f') . g'.
        // Write this composition as f'*g'
        Function<Float, P2<Float, String>> bindFComposeG = bindF.compose(g);

        Function<P2<Float, String>, P2<Float, String>> composedFG = bindF.compose(bindG);
        Function<P2<Float, String>, P2<Float, String>> composedFUnit = bindF.compose(bindUnit);

        Function<Float, P2<Float, String>> liftedF = lift(unit, anF);
        Function<Float, P2<Float, String>> liftedG = lift(unit, aG);

        Function<Float, Float> anFComposedAG = anF.compose(aG);
        Function<Float, P2<Float, String>> liftedComposition = lift(unit, anFComposedAG);

        // Show that lift f * lift g = lift (f.g)
        // liftedF * liftedG = liftedComposition;


    }

    public static <T, S> Function<T, P2<T, S>> lift(Function<T, P2<T, S>> unit, Function<T, T> anF) {
        //unit . f
        return unit.compose(anF);
    }

    //    we need to 'upgrade' f
//    bind must serve two purposes: it must
//    (1) apply f' to the correct part of g' x and
//    (2) concatenate the string returned by g' with the string returned by f'.
    public static <T> Function<P2<T, String>, P2<T, String>> bind(Function<T, P2<T, String>> f) {
        return floatStringP2 -> {
            final P2<T, String> apply = f.apply(floatStringP2._1());
            return P.p(apply._1(), apply._2().concat(floatStringP2._2()));
        };
    }

    //    we need to 'upgrade' f
//    bind must serve two purposes: it must
//    (1) apply f' to the correct part of g' x and
//    (2) concatenate the string returned by g' with the string returned by f'.
    public static abstract class Bind<T, S> implements Function<Function<T, P2<T, S>>, Function<P2<T, S>, P2<T, S>>> {

        private final BiFunction<S, S, S> concat;

        public Bind(BiFunction<S, S, S> concat) {
            this.concat = concat;
        }

        @Override
        public Function<P2<T, S>, P2<T, S>> apply(Function<T, P2<T, S>> f) {
            return p2P2Function -> {
                final P2<T, S> apply = f.apply(p2P2Function._1());
                return P.p(apply._1(), this.concat.apply(apply._2(), p2P2Function._2()));
            };
        }
    }

    public static class BindOnString<T> extends Bind<T, String> {
        public BindOnString() {
            super(String::concat);
        }
    }
}
