package isis4208.data_structures.suffix

class SuffixArraySearch(text: String): SuffixSearch(text) {

    val suffixes: IntArray

    /**
     * Compares suffixes by walking the original text character by character,
     * avoiding any String allocation during sort.
     */
    class PositionComparator(private val text: String): Comparator<Int> {
        override fun compare(ai: Int, bi: Int): Int {
            var i = ai
            var j = bi
            while (i < text.length && j < text.length) {
                val c = text[i] - text[j]
                if (c != 0) return c
                i++
                j++
            }
            // shorter suffix (fewer remaining chars) comes first
            return (text.length - i) - (text.length - j)
        }
    }

    init {
        val boxed = Array(text.length) { it }
        boxed.sortWith(PositionComparator(text))
        suffixes = boxed.toIntArray()
    }

    /**
     * This implementation uses Binary search over the suffixes structure for quick search
     */
    private fun search(query: String): Set<Int> {
        val res = mutableSetOf<Int>()
        if (query.isEmpty()) return res
        val N = query.length

        var l = 0
        var r = text.length
        //  Find the first suffix matching the query
        while (l < r) {
            val m = ((r - l) / 2) + l           //  In the current already lexicographic sorted array, search the middle
            val mi = suffixes[m]                 //  Get the index that the middle is pointing at
            if (mi + N <= text.length && text.substring(mi, mi + N) >= query) r = m
            else l = m + 1
        }
        //  Iterate and save all matching suffixes. In this case r is the first matching suffix
        while (
            r < suffixes.size &&
            text.length >= suffixes[r] + N &&
            text.substring(suffixes[r], suffixes[r] + N) == query
        ) {
            res.add(suffixes[r])
            r++
        }
        return res.toSet()
    }

    override fun search(queries: Set<String>): Map<String, Set<Int>> {
        val res = mutableMapOf<String, Set<Int>>()
        queries.forEach { query -> res[query] = search(query) }
        return res
    }
}
