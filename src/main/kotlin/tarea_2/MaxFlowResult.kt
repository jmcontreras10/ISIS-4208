package isis4208.tarea_2

/**
 * Max flow result as capacities - residual and max flow value
 */
data class MaxFlowResult(
    val maxFlow: Int,
    val flow: Array<IntArray>
)