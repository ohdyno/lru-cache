package cache.tests

import cache.main.LRUCache
import org.testng.Assert.assertEquals
import org.testng.Assert.assertNull
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * LRUCacheTests
 * Responsibility: Test the implementation of the LRU Cache
 */

class LRUCacheTests {
    private lateinit var cache: LRUCache

    @BeforeMethod
    fun setUp() {
        cache = LRUCache()
    }

    @Test
    fun capacityMatchesInitSize() {
        val size = 2
        cache.init(size = size)
        assertEquals(cache.capacity, size)
    }

    @Test
    fun getExistingKey() {
        cache.init(size = 1)
        val key = "foo"
        val value = "bar"
        cache.set(key, value)
        assertEquals(cache.get(key = key), value)
    }

    @Test
    fun getNonExistingKey() {
        cache.init(size = 1)
        val key = "foo"
        assertNull(cache.get(key = key))
    }

    @Test
    fun evictLeastRecentlySet() {
        cache.init(size = 1)
        val key = "keep"
        val value = "value"
        val evictKey = "evict"
        cache.set(evictKey, "notUsed")
        cache.set(key, value)
        assertNull(cache.get(evictKey))
        assertEquals(cache.get(key), value)
    }

    @Test
    fun evictLeastRecentlyGet() {
        cache.init(size = 2)
        val key = "keep"
        val value = "value"
        val evictKey = "evict"
        cache.set(key, value)
        cache.set(evictKey, "notUsed")
        cache.get(key)

        val otherKey = "otherKey"
        val otherValue = "otherValue"
        cache.set(otherKey, otherValue)

        assertNull(cache.get(evictKey))
        assertEquals(cache.get(key), value)
        assertEquals(cache.get(otherKey), otherValue)
    }

    @Test
    fun setSameKeyDoesNotTriggerEviction() {
        cache.init(size = 2)
        val key = "key"
        val value1 = "value1"
        val value2 = "value2"
        val shouldNotEvict = "shouldNotEvict"
        val shouldNotEvictValue = "shouldNotEvictValue"
        cache.set(shouldNotEvict, shouldNotEvictValue)
        cache.set(key, value1)
        cache.set(key, value2)

        assertEquals(cache.get(key), value2)
        assertEquals(cache.get(shouldNotEvict), shouldNotEvictValue)
    }
}