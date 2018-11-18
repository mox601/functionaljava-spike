import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.data.NonEmptyList
import arrow.data.Validated
import arrow.data.invalid
import arrow.data.valid
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.testng.annotations.Test
import kotlin.test.assertTrue

/**
 * Created by matteo (dot) moci (at) gmail (dot) com
 */

class ArrowTest {

    @Test
    fun testValidation() {
        val config = Config(mapOf("url" to "127.0.0.1", "port" to "1337"))

        val valid = parallelValidate(
                config.parse(Read.stringRead, "url"),
                config.parse(Read.intRead, "port")
        ) { url, port ->
            ConnectionParams(url, port)
        }
        assertTrue(valid.isValid)
    }

    data class ConnectionParams(val url: String, val port: Int)

    fun <E, A, B, C> parallelValidate(v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<NonEmptyList<E>, C> {
        return when {
            v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
            v1 is Validated.Valid && v2 is Validated.Invalid -> v2.toValidatedNel()
            v1 is Validated.Invalid && v2 is Validated.Valid -> v1.toValidatedNel()
            v1 is Validated.Invalid && v2 is Validated.Invalid -> Validated.Invalid(NonEmptyList(v1.e, listOf(v2.e)))
            else -> throw IllegalStateException("Not possible value")
        }
    }

    sealed class ConfigError {
        data class MissingConfig(val field: String) : ConfigError()
        data class ParseConfig(val field: String) : ConfigError()
    }

    abstract class Read<A> {

        abstract fun read(s: String): Option<A>

        companion object {

            val stringRead: Read<String> =
                    object : Read<String>() {
                        override fun read(s: String): Option<String> = Option(s)
                    }

            val intRead: Read<Int> =
                    object : Read<Int>() {
                        override fun read(s: String): Option<Int> =
                                if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
                    }
        }
    }

    data class Config(val map: Map<String, String>) {
        fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> {
            val v = Option.fromNullable(map[key])
            return when (v) {
                is Some -> {
                    val s = read.read(v.t)
                    when (s) {
                        is Some -> s.t.valid()
                        is None -> ConfigError.ParseConfig(key).invalid()
                    }
                }
                is None -> Validated.Invalid(ConfigError.MissingConfig(key))
            }
        }
    }

    @Test
    fun testCoroutines() {
        GlobalScope.launch {
            // launch new coroutine in background and continue
            delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
            println("World!") // print after delay
        }
        println("Hello,") // main thread continues while coroutine is delayed
        Thread.sleep(2000L) // block main thread for 2 seconds to keep JVM alive
    }
}