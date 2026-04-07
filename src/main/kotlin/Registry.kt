package isis4208

import isis4208.tarea_1.Problem5CheapestRoadUpgradesForTwoWayCityConnectivity
import isis4208.tarea_1.Problem1SixDegreesSolver
import isis4208.tarea_2.EdmondsKarpSolver
import isis4208.tarea_2.PushRelabelFrontSolver
import isis4208.tarea_4.Decompressor
import isis4208.tarea_4.HuffmanBasedCompressor
import isis4208.tarea_4.ShannonFanoBasedCompressor
import isis4208.tarea_5.TextSearch

object Registry {
    private val solvers: Map<Pair<String, String>, FileSolver> = mapOf(
        ("Tarea_1" to "1") to Problem1SixDegreesSolver(),
        ("Tarea_1" to "5") to Problem5CheapestRoadUpgradesForTwoWayCityConnectivity(),
        ("Tarea_2" to "1") to EdmondsKarpSolver(),
        ("Tarea_2" to "2") to PushRelabelFrontSolver(),
        ("Tarea_4" to "1") to ShannonFanoBasedCompressor(),
        ("Tarea_4" to "2") to HuffmanBasedCompressor(),
        ("Tarea_4" to "3") to Decompressor(),
        ("Tarea_5" to "1") to TextSearch()
    )

    fun get(assignment: String, problem: String): FileSolver? =
        solvers[assignment to problem]

    fun available(): String =
        solvers.keys
            .sortedWith(compareBy({ it.first }, { it.second }))
            .joinToString { (a, p) -> "$a:$p" }
}
