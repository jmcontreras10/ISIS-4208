package tarea_2

import isis4208.tarea_2.MaxFlowResult
import isis4208.tarea_2.pushRelabelFront
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PushRelabelFrontTest {

    private fun capMatrix(n: Int, edges: List<Triple<Int, Int, Int>>): Array<IntArray> {
        val m = Array(n) { IntArray(n) { 0 } }
        for ((u, v, c) in edges) m[u][v] = c
        return m
    }

    /**
     * Validates:
     * 1) 0 <= f(u,v) <= c(u,v) for all edges
     * 2) Flow conservation for all nodes except s and t
     * 3) Net flow out of s == maxFlow and net flow into t == maxFlow
     */
    private fun assertValidFlow(
        n: Int,
        caps: Array<IntArray>,
        result: MaxFlowResult,
        s: Int = 0,
        t: Int = n - 1
    ) {
        val f = result.flow

        // Capacity constraints + non-negativity on original edges
        for (u in 0 until n) {
            for (v in 0 until n) {
                if (caps[u][v] > 0) {
                    assertTrue(f[u][v] >= 0, "flow[$u][$v] must be >= 0")
                    assertTrue(f[u][v] <= caps[u][v], "flow[$u][$v] must be <= cap[$u][$v]")
                } else {
                    // Optional: if you only report flow for original edges, this should be 0.
                    // If your implementation might leave tiny bookkeeping values here, remove this check.
                    assertEquals(0, f[u][v], "flow[$u][$v] should be 0 when cap is 0")
                }
            }
        }

        // Flow conservation on intermediate nodes
        for (u in 0 until n) {
            if (u == s || u == t) continue
            var out = 0
            var `in` = 0
            for (v in 0 until n) {
                out += f[u][v]
                `in` += f[v][u]
            }
            assertEquals(`in`, out, "Flow conservation violated at node $u")
        }

        // Net flow value at s and t
        var outS = 0
        var inS = 0
        var inT = 0
        var outT = 0
        for (v in 0 until n) {
            outS += f[s][v]
            inS += f[v][s]
            inT += f[v][t]
            outT += f[t][v]
        }
        assertEquals(result.maxFlow, outS - inS, "Net outflow from source != maxFlow")
        assertEquals(result.maxFlow, inT - outT, "Net inflow to sink != maxFlow")
    }

    @Test
    fun `single edge returns its capacity`() {
        val n = 2
        val caps = capMatrix(n, listOf(Triple(0, 1, 7)))

        val result = pushRelabelFront(n, caps)

        assertEquals(7, result.maxFlow)
        assertValidFlow(n, caps, result)
    }

    @Test
    fun `no path to sink returns 0`() {
        val n = 3
        val caps = capMatrix(n, listOf(Triple(0, 1, 5))) // sink=2 unreachable

        val result = pushRelabelFront(n, caps)

        assertEquals(0, result.maxFlow)
        assertValidFlow(n, caps, result)
    }

    @Test
    fun `two disjoint paths sum`() {
        // 0->1->3 bottleneck 3
        // 0->2->3 bottleneck 2
        // total = 5
        val n = 4
        val caps = capMatrix(
            n,
            listOf(
                Triple(0, 1, 3),
                Triple(1, 3, 3),
                Triple(0, 2, 2),
                Triple(2, 3, 2),
            )
        )

        val result = pushRelabelFront(n, caps)

        assertEquals(5, result.maxFlow)
        assertValidFlow(n, caps, result)
    }

    @Test
    fun `CLRS classic example returns 24`() {
        val n = 6
        val caps = capMatrix(
            n,
            listOf(
                Triple(0, 1, 16),
                Triple(0, 2, 13),
                Triple(1, 2, 10),
                Triple(1, 3, 12),
                Triple(2, 3, 9),
                Triple(2, 4, 14),
                Triple(4, 3, 7),
                Triple(3, 5, 20),
                Triple(4, 5, 4),
            )
        )

        val result = pushRelabelFront(n, caps)

        assertEquals(24, result.maxFlow)
        assertValidFlow(n, caps, result)
    }

    @Test
    fun `sink bottleneck limits total flow`() {
        // Many ways into node 3, but only 7 out to sink
        val n = 5
        val caps = capMatrix(
            n,
            listOf(
                Triple(0, 1, 10),
                Triple(0, 2, 10),
                Triple(1, 3, 10),
                Triple(2, 3, 10),
                Triple(3, 4, 7),
            )
        )

        val result = pushRelabelFront(n, caps)

        assertEquals(7, result.maxFlow)
        assertValidFlow(n, caps, result)
    }
}
