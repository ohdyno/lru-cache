import cache.main.Cache
import cache.main.LRUCache
import cli.main.CLI
import commands.Commands
import commands.Results
import org.testng.Assert.assertEquals
import org.testng.Assert.assertTrue
import org.testng.annotations.Test
import java.util.*

/**
 * EndToEndTests
 * Responsibility:
 */
class EndToEndTests {
    private val commands = CommandsQueue()
    private val results = ResultsQueue()
    private val cache: Cache = LRUCache()
    private val cli: CLI = CLI(cache, commands, results)

    @Test
    fun example() {
        step("SIZE 3", "SIZE OK")
        step("GET foo", "NOTFOUND")
        step("SET foo 1", "SET OK")
        step("GET foo", "GOT 1")
        step("SET foo 1.1", "SET OK")
        step("GET foo", "GOT 1.1")
        step("SET bar 2", "SET OK")
        step("GET bar", "GOT 2")
        step("SET turkey sandwich", "SET OK")
        step("SET carrot four", "SET OK")
        step("GET foo", "NOTFOUND")
        step("GET bar", "GOT 2")
        step("GET turkey", "GOT sandwich")
        step("GET turkey carrot", "ERROR")
        step("GET carrot", "GOT four")
        step("EXIT")
        cli.start()
        assertTrue(results.completed)
    }

    private fun step(command: String) {
        commands.enqueue(command)
    }

    private fun step(command: String, result: String) {
        commands.enqueue(command)
        results.enqueue(command, result)
    }

    internal class ResultsQueue : Results {
        override fun complete() {
            if (completed)
                throw OutputAfterCompletionException()
            completed = true
        }

        override fun add(result: String) {
            if (completed)
                throw OutputAfterCompletionException()
            val expected = expectedResults.poll()
            assertEquals(result, expected.second, "Command: [${expected.first}]")
        }

        private val expectedResults: Queue<Pair<String, String>> = LinkedList()

        var completed: Boolean = false
            get() = expectedResults.isEmpty() && field

        fun enqueue(command: String, result: String) {
            expectedResults.offer(Pair(command, result))
        }
    }

    internal class CommandsQueue : Commands {
        private val queue: Queue<String> = LinkedList()

        override fun hasNext(): Boolean = queue.isNotEmpty()

        override fun next(): String = queue.poll()

        fun enqueue(command: String) {
            queue.offer(command)
        }

        fun peek(): String = queue.peek()
    }

    internal class OutputAfterCompletionException : Throwable()
}
