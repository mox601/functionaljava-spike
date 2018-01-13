package fm.mox.spikes.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.DotPrinter;
import com.github.javaparser.printer.JsonPrinter;
import lombok.extern.java.Log;
import org.testng.annotations.Test;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */
@Log
public class JavaParserTestCase {
    @Test(enabled = false)
    public void testName() throws Exception {
        CompilationUnit a = JavaParser.parse(
                "package fm.mox.classes; class A implements Serializable { public int sum(int a, B b) { return a; } }");
        JsonPrinter printer = new JsonPrinter(true);
        log.info(printer.output(a));
        DotPrinter dprinter = new DotPrinter(true);
        try (FileWriter fileWriter = new FileWriter("a.dot");
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(dprinter.output(a));
        }
    }
}
