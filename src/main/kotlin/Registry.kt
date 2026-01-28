package isis4208

import isis4208.tarea_1.Problem5CheapestRoadUpgradesForTwoWayCityConnectivity
import isis4208.tarea_1.Problem1SixDegreesSolver

object Registry {
    private val solvers: Map<Pair<String, String>, FileSolver> = mapOf(
        ("Tarea_1" to "1") to Problem1SixDegreesSolver(),
        ("Tarea_1" to "5") to Problem5CheapestRoadUpgradesForTwoWayCityConnectivity()
    )

    fun get(assignment: String, problem: String): FileSolver? =
        solvers[assignment to problem]

    fun available(): String =
        solvers.keys
            .sortedWith(compareBy({ it.first }, { it.second }))
            .joinToString { (a, p) -> "$a:$p" }
}
