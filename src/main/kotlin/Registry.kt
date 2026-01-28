package isis4208

import tarea_1.Problem1SixDegreesSolver

object Registry {
    private val solvers: Map<Pair<String, String>, FileSolver> = mapOf(
        ("Tarea_1" to "1") to Problem1SixDegreesSolver(),
        //("Tarea_1" to "5") to
    )

    fun get(assignment: String, problem: String): FileSolver? =
        solvers[assignment to problem]

    fun available(): String =
        solvers.keys
            .sortedWith(compareBy({ it.first }, { it.second }))
            .joinToString { (a, p) -> "$a:$p" }
}
