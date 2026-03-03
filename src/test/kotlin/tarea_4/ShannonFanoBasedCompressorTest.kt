package tarea_4

import isis4208.tarea_4.getShannonFanoCodeDictionary
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ShannonFanoBasedCompressorTest {
    @Test
    fun `should get Shannon Fano Codes Dictionary`() {
        val message = "KEBAB AB BAK"
        val N = message.length

        val ans = mapOf(
            'K' to 4U,
            'E' to 12U,
            'B' to 0U,
            'A' to 1U,
            ' ' to 5U
        )
        val dict = getShannonFanoCodeDictionary(message)
        for (c in dict.keys) assertEquals(ans[c], dict[c]!!.code, "Should be the same code")
    }
}