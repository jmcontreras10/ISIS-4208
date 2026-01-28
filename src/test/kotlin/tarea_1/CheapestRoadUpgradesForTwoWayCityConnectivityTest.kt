package tarea_1

import isis4208.tarea_1.Street
import isis4208.tarea_1.cheapestRoadUpgradesForTwoWayCityConnectivity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CheapestRoadUpgradesForTwoWayCityConnectivityTest {

    private fun undirectedKey(a: Int, b: Int): Pair<Int, Int> =
        if (a < b) a to b else b to a

    @Test
    fun `returns MST with expected edges and total cost`() {
        val streets = listOf(
            Street(1, 2, 1),
            Street(2, 3, 2),
            Street(3, 4, 1),
            Street(4, 5, 2),

            // extra edges (should NOT be selected)
            Street(1, 3, 10),
            Street(2, 4, 10),
            Street(3, 5, 10)
        )

        val mst: List<Street> = cheapestRoadUpgradesForTwoWayCityConnectivity(streets)

        // 1) Must have n-1 edges (n = 5 nodes)
        assertEquals(4, mst.size)

        // 2) Total cost must be minimal and match expected
        val totalCost = mst.sumOf { it.cost }
        assertEquals(6L, totalCost)

        // 3) Must contain exactly these undirected edges (ignore direction)
        val expected = setOf(
            undirectedKey(1, 2),
            undirectedKey(2, 3),
            undirectedKey(3, 4),
            undirectedKey(4, 5)
        )

        val actual = mst.map { undirectedKey(it.from, it.to) }.toSet()
        assertEquals(expected, actual)

        // 4) Sanity: all selected edges must be from input
        val inputSet = streets.map { Triple(it.from, it.to, it.cost) }.toSet()
        assertTrue(mst.all { Triple(it.from, it.to, it.cost) in inputSet })
    }
}