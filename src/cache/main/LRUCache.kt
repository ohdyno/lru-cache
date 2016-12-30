package cache.main

import java.util.*

/**
 * LRUCache
 * Responsibility: Implements a fix-sized cache that implements the least-recently-used (LRU) eviction policy.
 *
 * If init(capacity:) isn't explicitly called, the cache has a default capacity of 0.
 */
class LRUCache : Cache {
    private lateinit var items: LinkedList<Item>

    var capacity: Int = 0
        set(value) {
            field = value
            items = LinkedList()
        }

    init {
        init(size = 0)
    }

    override fun init(size: Int) {
        this.capacity = size
    }

    override fun set(key: String, value: String) {
        if (!allowSet())
            return

        val item = getItem(key)
        if (item != null) replace(item = item, value = value) else add(key, value)
    }

    private fun add(key: String, value: String) {
        if (full())
            evict()

        assert(items.size < capacity)
        items.addFirst(Item(key, value))
    }

    private fun replace(item: Item, value: String) {
        item.value = value
        reorderListGivenLastAccessed(item)
    }

    private fun allowSet(): Boolean {
        return capacity > 0
    }

    private fun evict() {
        items.removeLast()
    }

    private fun full() = items.size == capacity

    override fun get(key: String): String? {
        val item = getItem(key)
        if (item != null)
            reorderListGivenLastAccessed(item = item)

        return item?.value
    }

    private fun reorderListGivenLastAccessed(item: Item) {
        items.remove(item)
        items.addFirst(item)
    }

    private fun getItem(key: String) = items.firstOrNull { it.key == key }

    internal class Item(val key: String, var value: String) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as Item

            if (key != other.key) return false

            return true
        }

        override fun hashCode(): Int {
            return key.hashCode()
        }
    }

}
