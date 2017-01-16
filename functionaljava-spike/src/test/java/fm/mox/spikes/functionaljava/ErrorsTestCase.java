package fm.mox.spikes.functionaljava;

import com.diffplug.common.base.DurianPlugins;
import com.diffplug.common.base.Errors;
import com.diffplug.common.base.Throwing;

import java.util.Arrays;
import java.util.List;

/**
 * @author Matteo Moci ( matteo (dot) moci (at) gmail (dot) com )
 */
public class ErrorsTestCase implements Cloneable {

    /** Errors is a simple little class that makes ErrorHandling a lot easier. */
    public void whyItsGreat() throws Exception {
        List<Food> foodOnPlate = Arrays.asList(
                cook("salmon"),
                cook("asparagus"),
                cook("enterotoxin"));

        // without Errors, we have to write this
        foodOnPlate.forEach(val -> {
            try {
                eat(val);
            } catch (Barf e) {
                // get out the baking soda
            }
        });

        // With Errors, we can succinctly
        //                         sweep it under the rug
        foodOnPlate.forEach(Errors.suppress().wrap(this::eat));
        //                         save it for later
        foodOnPlate.forEach(Errors.log().wrap(this::eat));
        //                         make mom deal with it
        foodOnPlate.forEach(Errors.rethrow().wrap(this::eat));
        //                         ask the user deal with it
        foodOnPlate.forEach(Errors.dialog().wrap(this::eat));

        // We can also make our own Errorses, which can be reused across a project
        Errors retryHandler = Errors.createHandling(error -> {
            if (error instanceof Barf) {
                Food cause = ((Barf) error).looksLikeItUsedToBe();
                try {
                    eat(cause);
                } catch (Barf barf) {
                    // at least we tried really hard
                }
            }
        });
        foodOnPlate.forEach(retryHandler.wrap(this::eat));
    }

    public void whereDoLogAndDialogComeFrom() {
        // Errors.log() promises to "log", and Errors.dialog() promises to alert the user.
        // Doing those things is very different depending on whether your code is running as a web
        // application, console application, or desktop gui

        // One way around this ambiguity is to avoid using Errors.log() and Errors.dialog()
        // entirely, and only use your own custom Errors.  But if you are shipping a framework,
        // and your users might end up using Errors.log() and .dialog(), then you might want to
        // specify what those do.

        // By default, Errors.log() is just Throwable.printStackTrace, and Errors.dialog()
        // opens a JOptionPane. You can modify this behavior with the following:
        DurianPlugins.register(Errors.Plugins.Log.class, error -> {
            // log to twitter
        });
        DurianPlugins.register(Errors.Plugins.Dialog.class, error -> {
            // Headless application: email the sysadmin and exit
            // Web application: ajax an alert() to the user
        });
        // The trick is, you have to call these methods BEFORE Errors.log() or Errors.dialog()
        // are used anywhere in your whole application.  Once log() or dialog() have been used, they are
        // fixed for the duration of the runtime. If you're writing a library, then you probably shouldn't
        // try to change them.  If you're writing an application or framework, then you probably should.

        // If you're running in a JUnit environment, then you probably want any call to log() or dialog()
        // to kill the test. Setting the following system properties (again, before log() or dialog() are
        // called) will cause any errors to get wrapped and thrown as a java.lang.AssertionError.
        System.setProperty("durian.plugins.com.diffplug.common.base.Errors.Plugins.Log",
                "com.diffplug.common.base.Errors$Plugins$OnErrorThrowAssertion");
        System.setProperty("durian.plugins.com.diffplug.common.base.Errors.Plugins.Dialog",
                "com.diffplug.common.base.Errors$Plugins$OnErrorThrowAssertion");
    }

    @Override
    public Object clone() {
        // We'd like to return a Food,
        // but the only way to get it is the cook() method,
        // which throws a checked exception
        try {
            return cook("spaghetti");
        } catch (IAmOnFire e) {
            // TODO: water()
            return CEREAL;
        }

        // Our superclass doesn't let us propagate the exception,
        // so we've got to either
        //     A) return a default value
        //     B) rethrow the checked exception, wrapped in a RuntimeException
        //
        // If we decide to take the "default value" route, we might want to suppress
        // the memory of being on fire, or we might want to log it to twitter
    }

    @Override
    public void finalize() {
        // If we're taking the "default value" route, Errors has you covered
        Food logged = Errors.log().getWithDefault(() -> cook("spaghetti"), CEREAL); // log to twitter
        Food suppressed = Errors.suppress().getWithDefault(() -> cook("spaghetti"), CEREAL); // suppress to insecurity buffer

        // If we're taking the "rethrow RuntimeException" route, then specifying a
        // default value would be nonsensical, so we don't do it
        Food rethrow = Errors.rethrow().get(() -> cook("spaghetti"));

        // I'm stressed, don't judge me
        Errors.suppress().run(() -> {
            eat(logged);
            eat(suppressed);
            eat(rethrow);
        });
    }

    public void finalizeAdvanced() {
        // The previous example uncovered one of Errors's secrets. There are two
        // subclasses of Errors: Errors.Handling, and Errors.Rethrowing.

        // An instance of Errors.Rethrowing is an Errors that guarantees by construction
        // to always handle errors by throwing a RuntimeException.  This means that when it is returning
        // a value from a fallible function, it doesn't need a default value.
        Errors.Rethrowing rethrowing = Errors.createRethrowing(error -> {
            // Note that we're returning the exception, not throwing it.
            // The Errors will throw it for us, thus "guaranteed by construction".
            // It'd be okay if we threw it ourselves too, but it's not as pretty.
            return new RuntimeException("AHHHHHHHHHHHHHHHHHH!!!!");
        });
        Food thrown = rethrowing.get(() -> cook("spaghetti")); // no default needed

        // An instance of Errors.Handling is an Errors that doesn't make this guarantee.
        // Since it isn't going to handle errors by throwing an exception (well, it might, but it isn't
        // promising to, so it might not), a default value is required.
        Errors.Handling handling = Errors.createHandling(error -> {
            if (error instanceof RuntimeException) {
                throw (RuntimeException) error;
            } else {
                System.err.println("Hot! Hot! Hot! Hot!");
            }
        });
        Food handled = handling.getWithDefault(() -> cook("spaghetti"), CEREAL); // gotta have that default

        // A plain-old Errors can deal with functions that don't return values (namely Runnable and Consumer).
        // But to work with functions that do return a value (namely Supplier and Function), you're gonna
        // need to have it in its true Handling / Rethrowing form.  In a modern IDE with autocomplete,
        // The Right Thing will just automatically happen for you.
        if (thrown.equals(handled)) {
            // I guess I wasted my time with the two subclasses, because it didn't matter in the end.
        } else {
            // Time well spent.
        }
    }

    @SuppressWarnings("unused")
    public void wrapping() {
        // If your functional interface has 0 or 1 outputs, and 0 or 1 inputs, then Errors can wrap it into its standard Java 8 form
        Throwing.Runnable marathon = () -> { throw new IAmOnFire(); };
        java.lang.Runnable marathonSafe = Errors.log().wrap(marathon);

        Throwing.Consumer<Food> eat = this::eat;
        java.util.function.Consumer<Food> eatSafe = Errors.log().wrap(eat);

        Throwing.Supplier<Food> cookSpatula = () -> cook("spatula");
        java.util.function.Supplier<Food> cookSpatulaOrGetCereal = Errors.log().wrapWithDefault(cookSpatula, CEREAL);
        java.util.function.Supplier<Food> cookSpatulaOrBurn = Errors.rethrow().wrap(cookSpatula);

        Throwing.Function<String, Food> cookAnything = this::cook;
        java.util.function.Function<String, Food> cookAnythingOrGetCereal = Errors.log().wrapWithDefault(this::cook, CEREAL);
        java.util.function.Function<String, Food> cookAnythingOrBurn = Errors.rethrow().wrap(this::cook);

        // If your function has more than 1 input, you can either
        //    A) Make a wrapper function that calls Errors.get() to return a value (recommended)
        //    B) Make a "wrapper wrapper" (see https://github.com/diffplug/durian/blob/master/test/com/diffplug/common/base/ErrorsMultipleInputs.png)
        // If your function has more than 1 output, see https://github.com/diffplug/durian/blob/master/test/com/diffplug/common/base/ErrorsMultipleOutputs.png
    }

    @SuppressWarnings("unused")
    public void throwingSpecfic() {
        // Throwing.Specific lets you express functional interfaces which throw a specific exception
        Throwing.Specific.Consumer<Food, Barf> eatSignature = this::eat;
        Throwing.Specific.Function<String, Food, IAmOnFire> cookSignature = this::cook;

        // This can be helpful for writing generic code for working with a specific kind of exception, but
        // it isn't helpful for writing code for generic exceptions (because type erasure).
        //
        // The only way to know the type of List<T> is to grab an element out of the
        // list and call getClass() on it.  And then you still mostly don't know the type of the list.
        // This same limitation ripples through exception handling in the following way:
        class BarfHarness {
            // If you knew it was Barf at compile time then you can catch it
            void exceptionKnownAtCompileTime(Throwing.Specific.Runnable<Barf> eatAndThen) {
                try {
                    eatAndThen.run();
                } catch (Barf e) {
                    // and on Janitor's day too!
                }
            }

            // If the exception was generic at compile time, then you have to catch the most-general possible exception, which is Throwable
            <E extends Throwable> void exceptionGenericAtCompileTime(Throwing.Specific.Runnable<E> eatAndThen) {
                try {
                    eatAndThen.run();
                    // You can try "catch (E e)", but you'll get: "Cannot use the type parameter E in a catch block"
                    // You can try "catch (Barf e)", but you'll get: "Unreachable catch block for ErrorsExample.Barf. This exception is never thrown from the try statement body"
                } catch (Throwable e) {
                    // Looks like we're stuck catching Throwable.  Why should we bother having the exception be generic?
                }
            }
        }

        // Well, we probably shouldn't.  It didn't do us any good.
        // Throwing.Specific.* is most useful when it's implementing plain-old Throwing.*
        Throwing.Runnable runnableNonSpecific = () -> { throw new IAmOnFire(); };
        Throwing.Specific.Runnable<Throwable> runnable = runnableNonSpecific;

        // Here's the voluminous source code for Throwing:
        // public interface Runnable extends Specific.Runnable<Throwable> {}
        // public interface Supplier<T> extends Specific.Supplier<T, Throwable> {}
        // public interface Consumer<T> extends Specific.Consumer<T, Throwable> {}
        // public interface Function<T, R> extends Specific.Function<T, R, Throwable> {}
        // public interface Predicate<T> extends Specific.Predicate<T, Throwable> {}

        // Now you know how to cook spaghetti and eat salmon using Java >= 8 and generic exceptions using Errors!
    }

    /** Immutable, unfortunately. */
    private class IAmOnFire extends Exception {}

    /** A monoid over the group "food". */
    class Food {}

    /** Needs another day or two to flesh out some implementation details. */
    private class Barf extends Exception {
        public Food looksLikeItUsedToBe() {
            // TODO: Computer vision
            return null;
        }
    }

    /** Endofunctor of the monoid over the food. */
    void eat(Food food) throws Barf {}

    /** Returns the Hamming distance between the ingredients. */
    Food cook(String ingredients) throws IAmOnFire {
        return null;
    }

    /** This is obtained after stamping the natural numbers in ascending order on individual grains. */
    private static final Food CEREAL = null;
}
