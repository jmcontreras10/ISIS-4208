package isis4208.data_structures

/**
 * This data structure was build based on principles from:
 * Cormen, T.H., Leiserson, C.E., Rivest, R.L., Stein, C. Introduction to algorithms. MIT
 * Press, 3rd edition, 2009.
 * Chapter 21: Disjoint Sets
 */
class DSU (n: Int) {
    private val parent = IntArray(n) { it }
    private val rank = IntArray(n) {1}

    fun find(node: Int): Int {
        var x = node
        while (parent[x] != x) {
            x = parent[x]
        }
        return x
    }

    fun union(a: Int, b: Int): Boolean {
        var rootA = find(a)
        var rootB = find(b)

        if (rootA == rootB) return false

        if (rank[rootA] < rank[rootB]) {
            val tmp = rootA
            rootA = rootB
            rootB = tmp
        }

        parent[rootB] = rootA
        rank[rootA] += rank[rootB]
        return true
    }
}