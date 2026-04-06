package data_structures.suffix

import isis4208.data_structures.suffix.SuffixArraySearch
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue
import kotlin.test.expect

class SuffixArraySearchTest {

    @Nested
    inner class SortInit {
        @Test
        fun `sort test`(){
            val text = "Banana"
            val array = text.mapIndexed{ i, _ -> text.slice(i..<text.length) }.toTypedArray()
            array.forEach { print("$it, ") }
            array.sort()
            array.forEach { print("$it, ") }
            println("Finished")
        }

        @Test
        fun `should return sorted suffixes from given text`(){
            val text = "Banana"
            val suffixArray = SuffixArraySearch(text)
            val expectedOrder = intArrayOf(0,5,3,1,4,2)

            suffixArray.suffixes.forEachIndexed { i, e ->
                assertEquals(expectedOrder[i], e)
            }
        }

        @Test
        fun `should return sorted suffixes for text with repeated characters`() {
            val text = "mississippi"
            val suffixArray = SuffixArraySearch(text)
            // i(10), ippi(7), issippi(4), ississippi(1), mississippi(0),
            // pi(9), ppi(8), sippi(6), sissippi(3), ssippi(5), ssissippi(2)
            val expectedOrder = intArrayOf(10, 7, 4, 1, 0, 9, 8, 6, 3, 5, 2)

            assertContentEquals(expectedOrder, suffixArray.suffixes.toIntArray())
        }

        @Test
        fun `should return sorted suffixes for text with all same characters`() {
            val text = "aaaa"
            val suffixArray = SuffixArraySearch(text)
            // a(3), aa(2), aaa(1), aaaa(0)
            val expectedOrder = intArrayOf(3, 2, 1, 0)

            assertContentEquals(expectedOrder, suffixArray.suffixes.toIntArray())
        }

        @Test
        fun `should return sorted suffixes for single character text`() {
            val text = "a"
            val suffixArray = SuffixArraySearch(text)

            assertContentEquals(intArrayOf(0), suffixArray.suffixes.toIntArray())
        }
    }

    @Nested
    inner class Search {

        @Test
        fun `should return indices where query appears in text`() {
            val text = "Banana"
            val query = "ana"
            val suffixArray = SuffixArraySearch(text)
            val result = suffixArray.search(setOf(query))[query]
            /*
            0 - 0 Banana
            1 - 5 a
            2 - 3 ana
            3 - 1 anana
            4 - 4 na
            5 - 2 nana
             */

            val expected = setOf(3, 1)
            result!!.forEach { index ->
                assertTrue(expected.contains(index))
            }
        }

        @Test
        fun `should return indices where query appears in text v2`() {
            val text = "Esta es una banana de la Biologia. En la Biologia las Bananas son amarillas."
            val query = "Biologia"
            val suffixArray = SuffixArraySearch(text)
            val result = suffixArray.search(setOf(query))[query]

            val expected = setOf(25, 41)
            result!!.forEach { index ->
                assertTrue(expected.contains(index))
            }
        }

        @Test
        fun `should return empty, since no query present in the text`() {
            val text = "Esta es una banana de la Biologia. En la Biologia las Bananas son amarillas."
            val query = "Gato"
            val suffixArray = SuffixArraySearch(text)
            val result = suffixArray.search(setOf(query))[query]

            assertEquals(0, result!!.size)
        }

        @Test
        fun `should return empty, since empty query`() {
            val text = "Esta es una banana de la Biologia. En la Biologia las Bananas son amarillas."
            val query = ""
            val suffixArray = SuffixArraySearch(text)
            val result = suffixArray.search(setOf(query))[query]

            assertEquals(0, result!!.size)
        }
    }
}