package fm.mox.spikes.functionaljava;

import fj.F;
import fj.F1Functions;
import fj.F1W;
import fj.Unit;
import fj.data.IO;
import fj.data.IOFunctions;
import fj.data.IOW;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
public class IOTestCase {

    @Test
    public void name() throws Exception {
        final IO<Unit> askName = () -> {
            System.out.println("Hi, what's your name?");
            return Unit.unit();
        };

        IO<Unit> promptName = IOFunctions.stdoutPrint("Name: ");

        // we can compose these two values with fj.data.IOFunctions.append, since they
        // both are not interested in any runtime value

        IO<Unit> askAndPromptName = IOFunctions.append(askName, promptName);

        // now we create an IO value to read a line from stdin

        final IO<String> readName = () -> new BufferedReader(new InputStreamReader(System.in)).readLine();

        // this is the same as IOFunctions.stdinReadLine()

        // now we create a function which takes a string, upper cases it and creates
        // an IO value that would print the upper cased string if executed

        final F<String, IO<Unit>> upperCaseAndPrint = F1Functions.<String, IO<Unit>, String>o(IOFunctions::stdoutPrintln).f(String::toUpperCase);

        // we now want to compose reading the name with printing it, for that we need to
        // have access to the runtime value that is returned when the
        // IO value for read is executed, hence we use fj.data.IOFunctions.bind instead
        // of fj.data.IOFunctions.append

        final IO<Unit> readAndPrintUpperCasedName = IOFunctions.bind(readName, upperCaseAndPrint);

        // so append is really just a specialised form of bind, ignoring the runtime
        // value of the IO execution that was composed before us

        final IO<Unit> program = IOFunctions.bind(askAndPromptName, ignored -> readAndPrintUpperCasedName);

        // this is the same as writing IOFunctions.append(askAndPromptName, readAndPrintUpperCasedName)

        // we have recorded the entire program, but have not run anything yet
        // now we get to the small dirty part at the end of our program where we actually
        // execute it

        // we can either choose to just call program.run(), which allows the execution to escape
        // or we use safe to receive an fj.data.Either with the potential exception on the
        // left side

        IOFunctions.toSafeValidation(program).run().on((IOException e) -> {
            e.printStackTrace();
            return Unit.unit();
        });

    }

    public static void main(String[] args) throws Exception {
        // doing function composition like this can be quite cumbersome, since you will end
        // up nesting parenthesis unless you flatten it out by
        // assigning the functions to variables like above, but you can use the fj.F1W
        // syntax wrapper for composing single-argument functions and fj.data.IOW
        // for composing IO values instead, the entire program can be written like so:

        IOW.lift(IOFunctions.stdoutPrintln("What's your name again?"))
                .append(IOFunctions.stdoutPrint("Name: "))
                .append(IOFunctions.stdinReadLine())
                .bind(F1W.lift((F<String, String>) String::toUpperCase)
                        .andThen(IOFunctions::stdoutPrintln))
                .safe()
                .run()
                .on((IOException e) -> {
            e.printStackTrace();
            return Unit.unit();
        });
    }
}
