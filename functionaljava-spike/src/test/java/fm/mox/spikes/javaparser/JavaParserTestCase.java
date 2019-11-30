package fm.mox.spikes.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.DotPrinter;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Slf4j
public class JavaParserTestCase {
    @Test(enabled = false)
    public void testName() throws Exception {
        JavaParser javaParser = new JavaParser();
        ParseResult<CompilationUnit> a = javaParser.parse(
                "package fm.mox.classes; class A implements Serializable { public int sum(int a, B b) { return a; } }");
        DotPrinter dprinter = new DotPrinter(true);
        try (FileWriter fileWriter = new FileWriter("a.dot");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(dprinter.output(a.getResult().get()));
        }
    }
}
