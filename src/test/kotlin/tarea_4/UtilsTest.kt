package tarea_4

import isis4208.tarea_4.getProbabilities
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class UtilsTest {
    @Test
    fun `should get the frequencies correctly`(){
        val message = "KEBAB AB BAK"
        val N = message.length
        val res = mapOf(
            'K' to 2.0/N,
            'E' to 1.0/N,
            'B' to 4.0/N,
            'A' to 3.0/N,
            ' ' to 2.0/N
        )

        val freq = getProbabilities(message)
        for (c in freq.keys) assertEquals(res[c], freq[c], "Should be the same frequency")
    }
}