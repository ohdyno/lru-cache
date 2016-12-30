package cli.tests

import cache.main.Cache
import cli.main.CLI
import commands.Commands
import commands.Results
import org.mockito.Mockito.*
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

/**
 * CLITests
 * Responsibility: Test that cli.main.CLI correctly interfaces with the cache.main.Cache.
 */
class CLITests {
    private lateinit var cache: Cache
    private lateinit var application: CLI
    private lateinit var input: Commands
    private lateinit var output: Results

    @BeforeMethod
    fun setUp() {
        cache = mock(Cache::class.java)
        input = mock(Commands::class.java)
        output = mock(Results::class.java)
        application = CLI(cache, input, output)
    }

    @Test
    fun applicationExitsAfterReceivingExitCommand() {
        givenInputHas("EXIT")
        application.start()
        verify(output).complete()
        verifyNoMoreInteractions(output)
    }

    @Test
    fun applicationTellsCacheToInitWithSize() {
        val size = 1
        givenInputHas(command = "SIZE $size")
        application.start()
        verifyValidSize(size)
    }

    private fun givenInputHas(command: String) {
        `when`(input.hasNext()).thenReturn(true, false)
        `when`(input.next()).thenReturn(command)
    }

    @Test
    fun applicationTellsCacheToInitWithDifferentSize() {
        val size = 2
        givenInputHas(command = "SIZE $size")
        application.start()
        verifyValidSize(size)
    }

    private fun verifyValidSize(size: Int) {
        verify(cache).init(size = size)
        verify(output).add("SIZE OK")
        verifyNoMoreInteractions(output)
        verify(output, never()).complete()
    }

    @Test
    fun applicationTellsCacheToInitWithInvalidSize() {
        givenInputHas(command = "SIZE -1")
        application.start()
        verifyInvalidCommand()
        verify(cache, never()).init(anyInt())
    }

    private fun verifyInvalidCommand() {
        verify(output).add("ERROR")
        verifyNoMoreInteractions(output)
        verify(output, never()).complete()
    }

    @Test
    fun applicationTellsCacheToSetKeyValue() {
        val key = "foo"
        val value = "bar"
        givenInputHas(command = "SET $key $value")
        application.start()
        verify(cache).set(key = key, value = value)
        verifySetOK()
    }

    private fun verifySetOK() {
        verify(output).add("SET OK")
        verifyNoMoreInteractions(output)
        verify(output, never()).complete()
    }

    @Test
    fun applicationTellsCacheToSetWithNonNumericalCharacters() {
        val key = "1..121..21232$%$#key@#@$"
        val value = "1121..321...32132..#$%#value#@#$#%"
        givenInputHas(command = "SET $key $value")
        application.start()
        verify(cache).set(key = key, value = value)
        verifySetOK()
    }

    @Test
    fun applicationTellsCacheToGetNonExistentKey() {
        val key = "key"
        givenInputHas(command = "GET $key")
        application.start()
        verify(cache).get(key = key)
        verify(output).add("NOTFOUND")
        verifyNoMoreInteractions(output)
        verify(output, never()).complete()
    }

    @Test
    fun applicationTellsCacheToGetExistingKey() {
        val key = "key"
        val value = "value"
        givenInputHas(command = "GET $key")
        `when`(cache.get(key)).thenReturn(value)
        application.start()
        verify(cache).get(key = key)
        verify(output).add("GOT $value")
        verifyNoMoreInteractions(output)
        verify(output, never()).complete()
    }

    @Test
    fun applicationTellsCacheToGetNonAlphanumericKey() {
        val key = "1..121..21232$%$#key@#@$"
        val value = "value"
        givenInputHas(command = "GET $key")
        `when`(cache.get(key)).thenReturn(value)
        application.start()
        verify(cache).get(key = key)
        verify(output).add("GOT $value")
        verifyNoMoreInteractions(output)
        verify(output, never()).complete()
    }
}