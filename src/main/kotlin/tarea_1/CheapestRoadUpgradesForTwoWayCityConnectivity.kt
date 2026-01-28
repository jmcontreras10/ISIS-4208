package isis4208.tarea_1

import isis4208.FileSolver
import isis4208.data_structures.DSU
import java.io.File

// =================================================================
//                        Algorithm: Kruskal
// =================================================================

/**
 * Actual algorithm
 * In: List<Street>     -> Representation of the streets' intersections as (from, to, cost) tuple.
 * Out: List<Street>    -> Representation of the double direction streets MST
 *
 * Explanation:
 * To minimize the cost of connecting all the city with double direction streets,
 * we run Kruskal algorithm to calculate the MST of the given graph.
 *
 * For this some preprocessing is done to parse the raw data of the input into an indexed undirected graph.
 * We assume no street departs from an intersection and ends in the same, nor double direction streets exists yet.
 * The resulting undirected graph is represented by the list of edges,
 * their cost and the original edge representation from input.
 * The edges are sorted to apply a greedy strategy minimizing the cost.
 * Using Disjoint Set union we add build the mst by joining not connected vertexes of an edge avoiding cycles.
 * Finally, once we have n-1 edges, the mst is built and connected so we return it.
 */
fun cheapestRoadUpgradesForTwoWayCityConnectivity(streets: List<Street>): List<Street> {
    val ids = LinkedHashSet<Int>()
    for (street in streets) {
        ids.add(street.from)
        ids.add(street.to)
    }

    val idToIndex = HashMap<Int, Int>(ids.size * 2)
    var idx = 0
    for (id in ids) idToIndex[id] = idx++
    val n = ids.size

    val edges = streets
        .asSequence()
        .map { street ->
            val u = idToIndex[street.from]!!
            val v = idToIndex[street.to]!!
            Edge(
                minOf(u,v),
                maxOf(u,v),
                street.cost,
                Street(street.from, street.to, street.cost)
            )
        }
        .sortedBy { it.cost }
        .toList()

    val dsu = DSU(n)
    val mst = ArrayList<Street>()

    for (edge in edges) {
        if (dsu.union(edge.u, edge.v)) {
            mst.add(edge.upgrade)
            if (mst.size == n - 1) break
        }
    }
    return mst
}

// =================================================================
//                              Data types
// =================================================================

/**
 * This represents the Street as it comes from raw data.
 */
data class Street (
    val from: Int,
    val to: Int,
    val cost: Long,
)

/**
 * This is a mapped representation of a Street, by replacing the form, to ids
 * to indexes in an array to simplify the code.
 * Also, this representation is of a undirected edge, which means is no from or to anymore, but vertex.
 */
private data class Edge(
    val u: Int,
    val v: Int,
    val cost: Long,
    val upgrade: Street
)

// =================================================================
//                  Problem Solver
// =================================================================

class Problem5CheapestRoadUpgradesForTwoWayCityConnectivity : FileSolver {
    /**
     * Function for read the csv input file
     */
    private fun readStreetsFromCsv(file: File): List<Street> {
        val lines = file.readLines(Charsets.UTF_8)
        val streets = ArrayList<Street>()

        for (i in 1 until lines.size) {
            val vars = lines[i].split(",")

            val from = vars[0].toIntOrNull() ?: error("Invalid from id at line ${i + 1}")
            val to = vars[1].toIntOrNull() ?: error("Invalid from id at line ${i + 1}")
            val cost = vars[2].toLongOrNull() ?: error("Invalid cost at line ${i + 1}")
            streets.add(
                Street(
                    from = from,
                    to = to,
                    cost = cost
                )
            )
        }
        return streets
    }

    /**
     * Function for write the csv output file
     */
    private fun writeUpgradesToCsv(inputFile: File, upgrades: List<Street>, outputPath: String? = null): File {
        val outFile = if (outputPath.isNullOrBlank()) {
            File("outputs/Tarea_1/p5_mst.csv")
        } else {
            File(outputPath)
        }

        val sb = StringBuilder()
        sb.appendLine("from,to,cost")
        for (u in upgrades) {
            sb.append(u.from).append(',')
            sb.append(u.to).append(',')
            sb.append(u.cost)
                .appendLine()
        }
        outFile.writeText(sb.toString(), Charsets.UTF_8)
        return outFile
    }

    override fun solve(inputFile: File, outputPath: String?): String {
        val streets = readStreetsFromCsv(inputFile)
        val upgrades = cheapestRoadUpgradesForTwoWayCityConnectivity(streets)

        val outFile = writeUpgradesToCsv(inputFile, upgrades, outputPath)
        val total = upgrades.sumOf { it.cost }

        return buildString {
            appendLine("MST upgrades: ${upgrades.size}")
            appendLine("Total cost: $total")
            appendLine("Output written to: ${outFile.path}")
        }
    }
}