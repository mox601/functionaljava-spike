import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.getOrElse
import org.testng.annotations.Test
import kotlin.test.assertEquals

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */

class ArrowTest {

    fun double(x: Int): Int {
        return 2 * x
    }

    @Test
    fun testDouble() {
        val value1 = maybeItWillReturnSomething(true)
        val value2 = maybeItWillReturnSomething(false)
        assertEquals(4, double(2))
        assertEquals("No value", value2.getOrElse { "No value" })
        assertEquals("Found value", value1.getOrElse { "No value" })
    }

    fun maybeItWillReturnSomething(flag: Boolean): Option<String> =
            if (flag) Some("Found value") else None
}