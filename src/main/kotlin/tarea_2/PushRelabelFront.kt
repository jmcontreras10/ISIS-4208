package isis4208.tarea_2

import isis4208.FileSolver
import java.io.File
import java.util.*

// =================================================================
//                        Algorithm: Edmonds-Karp
// =================================================================

/**
 * Actual algorithm
 * In:
 * -  N: Int                        -> Number of nodes in the graph
 * -  capacities: Array<IntArray>   -> Capacities matrix capacities[i][j] = capacity from i to j
 * Out: MaxFlowResult               -> Aggregated object, solution for this problem: max capacity
 *
 * Explanation:
 * First some data structures need to be set:
 *  - flow[u][v]: to store the actual flow
 *  - height[u]: h(u) = height of u
 *  - excess[u]: e[u] = excess accumulated in u
 *  - L: list of vertices excluding s and t
 *    Since Push-Relabel to front uses an order to discharge vertices and by sending the relabeled ones to the
 *    beginning or "to front", a list L is used to iterate vertices. If a vertex u is relabeled (its height increases),
 *    u is moved to the front of L and the scan restarts from the beginning.
 *  - current[u]: This works like a pointer to easily keep track of current neighbor visiting.
 *
 * Then the algorithm starts by setting the pre-flow as follows:
 *  - h(s) = N
 *  - for all u != s: h(u) = 0
 *  - for all u: excess(u) = 0
 *
 * After that, the first push from source s is performed, pushing as much flow as possible to its neighbors.
 * Naturally this adds excess to some nodes.
 *
 * Then the main discharge process starts by iterating vertices u in L:
 *  - Discharge u while excess(u) > 0 by trying to push flow to neighbors v that comply:
 *        residual[u][v] > 0  and  h(u) = h(v) + 1
 *    using current[u] to avoid re-scanning neighbors from the beginning.
 *  - If no neighbor complies and excess(u) > 0, relabel u:
 *        h(u) = 1 + min{ h(v) | residual[u][v] > 0 }
 *    and restart scanning its neighbors.
 *  - If after discharging u its height increased (u was relabeled), move u to the front of L.
 *
 * Once there is no more excess in any node different from s or t, all the remaining excess should be on t, and
 * that would be the maximumFlow.
 * So return the excess[t] as maxFlow and flow.
 */
fun pushRelabelFront(N: Int, capacities: Array<IntArray>): MaxFlowResult {
    val residual = Array(N) { i -> capacities[i].clone()}

    // Source s, sink t
    val s = 0
    val t = N - 1

    val flow = Array(N) { IntArray(N) {0} }
    val height = IntArray(N) { 0 }
    val excess = IntArray(N) { 0 }

    val current = IntArray(N) { 0 }

    /**
     * Push as much as possible from u to v.
     * This is limited by excess in u and residual capacity on u -> v
     */
    fun push(u: Int, v: Int) {
        val delta = minOf(excess[u], residual[u][v])
        if (delta <= 0) return

        residual[u][v] -= delta
        residual[v][u] += delta

        excess[u] -= delta
        excess[v] += delta

        if (capacities[u][v] > 0) flow[u][v] += delta
        else flow[v][u] -= delta
    }

    /**
     * Re-compute height of u as 1 + min(h_f(v)), in this case being h_f(v) all the neighbors with residual capacity
     * enough to receive charge. Once relabel process ends, start trying to discharge u from first node.
     */
    fun relabel(u: Int) {
        var minHeight = Int.MAX_VALUE
        for (v in 0  until N) {
            if (residual[u][v] > 0) {
                minHeight = minOf(minHeight, height[v])
            }
        }
        if (minHeight < Int.MAX_VALUE) {
            height[u] = minHeight + 1
            current[u] = 0
        }
    }

    // Pre-flow set
    height[s] = N   //  h(s) = N

    //  First push
    for (v in 0 until N) {
        val cap = residual[s][v]
        if (cap > 0) {
            residual[s][v] -= cap
            residual[v][s] += cap

            flow[s][v] += cap
            excess[v] += cap
            excess[s] -= cap
        }
    }

    //  Main discharge loop
    val L = mutableListOf<Int>()
    for (u in 0 until N) {
        if (u != s && u != t) L.add(u)
    }
    var index = 0
    while (index < L.size) {
        val u = L[index]
        val oldHeight = height[u]

        while (excess[u] > 0) {
            if (current[u] == N) {
                relabel(u)
                continue
            }

            val v = current[u]

            if (residual[u][v] > 0 && height[u] == height[v] + 1)  push(u, v)
            else current[u]++
        }

        if (height[u] > oldHeight) {
            L.removeAt(index)
            L.add(0, u)
            index = 0
        } else {
            index++
        }
    }

    return MaxFlowResult(excess[t], flow)
}

// =================================================================
//                  Problem Solver
// =================================================================

class PushRelabelFrontSolver : FileSolver {

    private var N = 0
    private var capacities: Array<IntArray> = arrayOf()

    override fun solve(inputFile: File, outputPath: String?): String {
        val loadResults = loadCapacitiesFromFile(inputFile)
        N = loadResults.first
        capacities = loadResults.second

        val start = System.nanoTime()
        val maxFlow = pushRelabelFront(N, capacities)
        val elapsedMs = (System.nanoTime() - start) / 1_000_000.0
        val outFile = writeMaxFlowOutput(inputFile, elapsedMs, maxFlow, outputPath, N, capacities)

        return buildString {
            appendLine("MaxFlow: ${maxFlow.maxFlow}")
            appendLine("Execution time: ${"%.3f".format(elapsedMs)} ms")
            appendLine("Output written to: ${outFile.path}")
        }
    }
}
