package isis4208.tarea_2

import java.io.File
import java.util.*

fun loadCapacitiesFromFile(file: File): Pair<Int, Array<IntArray>> {
    var N = 0
    var capacities: Array<IntArray>

    file.bufferedReader().useLines { lines ->
        val it = lines.iterator()

        if(!it.hasNext()) throw Error("Empty file")
        val nLine = it.next()
        N = nLine.toIntOrNull() ?: throw Error("Wrong format, first line should be a number")

        capacities = Array(N) { IntArray(N) {0} }
        it.forEachRemaining { line ->
            if (line.isEmpty()) return@forEachRemaining

            val edge = line.split(" ")
            if(edge.size != 3) throw Error("Wrong format \'${edge}\', should be \"source end capacity\"")

            val u = edge[0].toIntOrNull() ?:
            throw Error("Wrong format \'${edge}\', first line should be a number")
            val v = edge[1].toIntOrNull() ?:
            throw Error("Wrong format \'${edge}\', first line should be a number")
            val c = edge[2].toIntOrNull() ?:
            throw Error("Wrong format \'${edge}\', first line should be a number")

            capacities[u][v] = c
        }
    }
    return Pair(N, capacities)
}

fun writeMaxFlowOutput(
    inputFile: File,
    executionTimeMs: Double,
    result: MaxFlowResult,
    outputPath: String? = null,
    N: Int,
    capacities: Array<IntArray>,
): File {
    val outFile = if (outputPath.isNullOrBlank()) {
        val baseName = inputFile.nameWithoutExtension
        File("outputs/Tarea_2/${baseName}_output.txt")
    } else {
        File(outputPath)
    }

    outFile.parentFile?.mkdirs()

    val sb = StringBuilder()

    sb.appendLine("Execution time: ${String.format(Locale.US, "%.3f", executionTimeMs)}")
    sb.appendLine("Max flow: ${result.maxFlow}")

    for (u in 0 until N) {
        for (v in 0 until N) {
            if (capacities[u][v] > 0) {
                sb.append(u).append(' ')
                sb.append(v).append(' ')
                sb.append(result.flow[u][v])
                sb.appendLine()
            }
        }
    }

    outFile.writeText(sb.toString(), Charsets.UTF_8)
    return outFile
}