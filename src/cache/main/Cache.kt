package cache.main

interface Cache {
    fun init(size: Int)
    fun set(key: String, value: String)
    fun get(key: String): String?
}