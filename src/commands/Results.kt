package commands

interface Results {
    fun complete()
    fun add(result: String)
}