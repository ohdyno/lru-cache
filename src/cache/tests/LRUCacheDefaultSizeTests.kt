package cache.tests

import cache.main.LRUCache
import org.testng.Assert.assertNull
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

class LRUCacheDefaultSizeTests {
    private lateinit var cache: LRUCache

    @BeforeMethod
    fun setUp() {
        cache = LRUCache()
    }

    @Test
    fun failToSetKey() {
        cache.set("key", "value")
        assertNull(cache.get("key"))
    }
}