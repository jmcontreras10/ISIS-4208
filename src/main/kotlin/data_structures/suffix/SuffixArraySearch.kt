package isis4208.data_structures.suffix

class SuffixArraySearch(text: String) : SuffixSearch(text) {

    val suffixes: IntArray

    /**
     * lcp[i] = length of the longest common prefix between SA[i-1] and SA[i].
     * lcp[0] = 0 by convention.
     * Built in O(n) via Kasai's algorithm.
     */
    val lcp: IntArray

    // Sparse table for O(1) range-minimum queries on lcp[].
    private val log2: IntArray
    private val sparse: Array<IntArray>

    /**
     * Compares suffixes by walking the original text character by character,
     * avoiding any String allocation during sort.
     */
    class PositionComparator(private val text: String) : Comparator<Int> {
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
        val n = text.length

        // ── Suffix array ──────────────────────────────────────────────
        val boxed = Array(n) { it }
        boxed.sortWith(PositionComparator(text))
        suffixes = boxed.toIntArray()

        // ── LCP array (Kasai O(n)) ────────────────────────────────────
        lcp = buildLCP()

        // ── Sparse table for RMQ ──────────────────────────────────────
        log2 = IntArray(n + 1)
        for (i in 2..n) log2[i] = log2[i / 2] + 1
        val LOG = log2[n]

        sparse = Array(LOG + 1) { IntArray(n) }
        for (i in 0 until n) sparse[0][i] = lcp[i]
        for (k in 1..LOG) {
            for (i in 0..n - (1 shl k)) {
                sparse[k][i] = minOf(sparse[k - 1][i], sparse[k - 1][i + (1 shl (k - 1))])
            }
        }
    }

    // ── Private helpers ───────────────────────────────────────────────

    /** Kasai's algorithm – builds lcp[] in O(n). */
    private fun buildLCP(): IntArray {
        val n = text.length
        val rank = IntArray(n)
        for (i in 0 until n) rank[suffixes[i]] = i
        val lcp = IntArray(n)
        var h = 0
        for (i in 0 until n) {
            if (rank[i] > 0) {
                val j = suffixes[rank[i] - 1]
                while (i + h < n && j + h < n && text[i + h] == text[j + h]) h++
                lcp[rank[i]] = h
                if (h > 0) h--
            }
        }
        return lcp
    }

    /** min(lcp[a..b]) in O(1) via sparse table. */
    private fun rmq(a: Int, b: Int): Int {
        if (a > b) return Int.MAX_VALUE
        val len = b - a + 1
        val k = log2[len]
        return minOf(sparse[k][a], sparse[k][b - (1 shl k) + 1])
    }

    /**
     * lcp(SA[i], SA[j]) = min(lcp[i+1 .. j])  (for i < j).
     * Uses the identity that the LCP of two suffixes equals the minimum
     * of the adjacent-LCP values between them in sorted order.
     */
    private fun lcpBetween(i: Int, j: Int): Int {
        if (i == j) return text.length
        val lo = minOf(i, j)
        val hi = maxOf(i, j)
        return rmq(lo + 1, hi)
    }

    /**
     * LCP-accelerated lower bound (Manber & Myers).
     *
     * Returns the first index r such that SA[r] >= query in prefix-comparison
     * sense (i.e., SA[r] starts with query, or SA[r] is lex-greater than query).
     *
     * Invariant:
     *   SA[lo] < query  (lo == -1 is a −∞ sentinel)
     *   SA[hi] >= query (hi == n  is a +∞ sentinel)
     *   llcp  = lcp(query, SA[lo]); 0 when lo == -1
     *
     * Key step: compute ML = lcp(SA[lo], SA[mid]) via RMQ.
     *   ML > llcp  →  query > SA[mid]  (no char comparison needed)
     *   ML < llcp  →  query < SA[mid]  (no char comparison needed)
     *   ML == llcp →  compare from position llcp onward
     *
     * Total character comparisons across all steps: O(m + log n).
     */
    private fun lowerBound(query: String): Int {
        val m = query.length
        val n = suffixes.size
        var lo = -1
        var hi = n
        var llcp = 0  // lcp(query, SA[lo]); 0 for sentinel

        while (hi - lo > 1) {
            val mid = (lo + hi) ushr 1
            val si = suffixes[mid]
            val ML = if (lo >= 0) lcpBetween(lo, mid) else 0

            when {
                ML > llcp -> lo = mid           // query > SA[mid]; llcp unchanged
                ML < llcp -> hi = mid           // query < SA[mid]
                else -> {
                    // ML == llcp: compare characters from position llcp onwards
                    var k = llcp
                    while (k < m && si + k < text.length && query[k] == text[si + k]) k++

                    if (k == m || (si + k < text.length && query[k] < text[si + k])) {
                        // query is a prefix of SA[mid], or query < SA[mid] → go left
                        hi = mid
                    } else {
                        // SA[mid] is a proper prefix of query, or query > SA[mid] → go right
                        lo = mid
                        llcp = k
                    }
                }
            }
        }
        return hi
    }

    /**
     * This implementation uses LCP-accelerated binary search for O(m + log n)
     * character comparisons, followed by a linear scan to collect all matches.
     */
    private fun search(query: String): Set<Int> {
        if (query.isEmpty()) return emptySet()
        val m = query.length
        val n = suffixes.size

        val lb = lowerBound(query)

        val res = mutableSetOf<Int>()
        var r = lb
        while (r < n &&
               suffixes[r] + m <= text.length &&
               text.substring(suffixes[r], suffixes[r] + m) == query) {
            res.add(suffixes[r])
            r++
        }
        return res
    }

    override fun search(queries: Set<String>): Map<String, Set<Int>> =
        queries.associateWith { search(it) }
}
