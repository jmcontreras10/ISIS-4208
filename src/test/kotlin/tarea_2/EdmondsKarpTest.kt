package tarea_2

import isis4208.tarea_2.MaxFlowResult
import isis4208.tarea_2.edmondsKarp
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EdmondsKarpTest {

    private fun capMatrix(n: Int, edges: List<Triple<Int, Int, Int>>): Array<IntArray> {
        val m = Array(n) { IntArray(n) { 0 } }
        for ((u, v, c) in edges) {
            m[u][v] = c
        }
        return m
    }

    @Test
    fun `single edge returns its capacity and correct edge flow`() {
        val n = 2
        val caps = capMatrix(n, listOf(Triple(0, 1, 7)))

        val result: MaxFlowResult = edmondsKarp(n, caps)

        assertEquals(7, result.maxFlow)
        assertEquals(7, result.flow[0][1])
    }

    @Test
    fun `no path to sink returns 0`() {
        val n = 3
        val caps = capMatrix(n, listOf(Triple(0, 1, 5))) // sink=2 unreachable

        val result = edmondsKarp(n, caps)

        assertEquals(0, result.maxFlow)
        // no edge into sink => flows should be 0 there
        assertEquals(0, result.flow[0][1]) // could be 0 or 5? Actually can't reach sink, so must be 0
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

        val result = edmondsKarp(n, caps)

        assertEquals(5, result.maxFlow)

        // Optional: verify per-edge flows exactly (unique solution here)
        assertEquals(3, result.flow[0][1])
        assertEquals(3, result.flow[1][3])
        assertEquals(2, result.flow[0][2])
        assertEquals(2, result.flow[2][3])
    }

    @Test
    fun `CLRS classic example returns 23`() {
        // Nodes: s=0, t=5
        // Expected max flow = 23
        val n = 6
        val caps = capMatrix(
            n,
            listOf(
                Triple(0, 1, 16),
                Triple(0, 2, 13),
                Triple(1, 2, 10),
                Triple(2, 1, 4),
                Triple(1, 3, 12),
                Triple(3, 2, 9),
                Triple(2, 4, 14),
                Triple(4, 3, 7),
                Triple(3, 5, 20),
                Triple(4, 5, 4),
            )
        )

        val result = edmondsKarp(n, caps)

        assertEquals(23, result.maxFlow)

        // For this network, max flow is unique in value but the exact edge flows can vary by path choices.
        // So we DO NOT assert exact per-edge values here.
        // Instead, assert flow conservation + capacity constraints if you want stronger tests.
    }

    @Test
    fun `sink bottleneck limits total flow`() {
        // Many ways into node 3, but only 7 out to sink
        // Max flow should be 7
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

        val result = edmondsKarp(n, caps)

        assertEquals(7, result.maxFlow)
        assertEquals(7, result.flow[3][4]) // must saturate the bottleneck edge
    }
}
