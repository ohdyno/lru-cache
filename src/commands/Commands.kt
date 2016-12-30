package commands

interface Commands {
    fun hasNext(): Boolean
    fun next(): String
}