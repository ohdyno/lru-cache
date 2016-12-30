package cli.main

import cache.main.Cache
import commands.Command
import commands.Commands
import commands.Results
import java.util.*

class CLI(val cache: Cache, val input: Commands = CLI.StdIn, val output: Results = CLI.StdOut) {
    private var shouldContinue: Boolean = true

    fun start() {
        shouldContinue = true
        while (input.hasNext() && shouldContinue) {
            process(input.next())
        }
    }

    private fun process(command: String) {
        when (getCommandType(command = command)) {
            Command.Size -> processSize(command)
            Command.Set -> processSet(command)
            Command.Exit -> processExit()
            Command.Invalid -> processInvalid()
            Command.Get -> processGet(command)
        }
    }

    private fun processGet(command: String) {
        val results = GET_PATTERN.matchEntire(input = command)
        if (results != null) {
            val key = results.groupValues[1]
            val result = cache.get(key = key)
            if (result == null)
                output.add("NOTFOUND")
            else
                output.add("GOT $result")
        }
    }

    private val SIZE_PATTERN = "^SIZE (\\d+)$".toRegex()
    private val SET_PATTERN = "^SET (\\S+) (\\S+)$".toRegex()
    private val EXIT_PATTERN = "^EXIT$".toRegex()
    private val GET_PATTERN = "^GET (\\S+)$".toRegex()

    private fun getCommandType(command: String): Command {
        if (command.matches(SIZE_PATTERN))
            return Command.Size
        if (command.matches(SET_PATTERN))
            return Command.Set
        if (command.matches(EXIT_PATTERN))
            return Command.Exit
        if (command.matches(GET_PATTERN))
            return Command.Get
        return Command.Invalid
    }

    private fun processSize(command: String) {
        val results = SIZE_PATTERN.matchEntire(input = command)
        if (results != null) {
            val size: Int = results.groupValues[1].toInt()
            cache.init(size = size)
            output.add("SIZE OK")
        }
    }

    private fun processSet(command: String) {
        val results: MatchResult? = SET_PATTERN.matchEntire(input = command)
        if (results != null) {
            cache.set(key = results.groupValues[1], value = results.groupValues[2])
            output.add("SET OK")
        }
    }

    private fun processExit() {
        shouldContinue = false
        output.complete()
    }

    private fun processInvalid() {
        output.add("ERROR")
    }

    object StdIn : Commands {
        private val scanner: Scanner = Scanner(System.`in`)
        override fun hasNext(): Boolean = scanner.hasNextLine()
        override fun next(): String = scanner.nextLine()
    }

    object StdOut : Results {
        override fun complete() = System.exit(0)
        override fun add(result: String) = System.out.println(result)
    }
}