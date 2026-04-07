package isis4208.tarea_5

import isis4208.data_structures.suffix.SuffixArraySearch
import java.io.File
import kotlin.random.Random
import kotlin.system.measureTimeMillis

// =================================================================
//  Suffix Array Benchmark
//  Run: java -cp build/libs/isis4208.jar isis4208.tarea_5.BenchmarkKt
// =================================================================

private val TEXT_SIZES  = listOf(100_000, 1_000_000, 10_000_000)
private val QUERY_SIZES = listOf(1_000, 10_000, 100_000, 1_000_000)

fun main() {
    val outputFile = File("outputs/Tarea_5/benchmark_results.csv")
    outputFile.parentFile.mkdirs()

    data class Row(
        val textSize: Int, val queryCount: Int,
        val buildMs: Long, val searchMs: Long
    )

    val rows = mutableListOf<Row>()

    for (textSize in TEXT_SIZES) {
        print("Generating text ($textSize chars)... ")
        val text = generateText(textSize)
        println("done")

        for (querySize in QUERY_SIZES) {
            print("  queries=$querySize → building suffix array... ")
            val queries = generateQueries(text, querySize)

            val buildMs = measureTimeMillis { SuffixArraySearch(text) }
            val searcher = SuffixArraySearch(text)   // re-build cleanly for search timing

            print("${buildMs}ms | searching... ")
            val searchMs = measureTimeMillis { searcher.search(queries) }
            println("${searchMs}ms")

            rows.add(Row(textSize, queries.size, buildMs, searchMs))
        }
    }

    // ── CSV output ────────────────────────────────────────────────
    val csv = buildString {
        appendLine("text_size,query_count,build_ms,search_ms,total_ms")
        rows.forEach { r ->
            appendLine("${r.textSize},${r.queryCount},${r.buildMs},${r.searchMs},${r.buildMs + r.searchMs}")
        }
    }
    outputFile.writeText(csv)

    // ── Markdown table (for README / report) ─────────────────────
    println()
    println("## Results")
    println()
    println("| Text size | Queries | Build (ms) | Search (ms) | Total (ms) |")
    println("|----------:|--------:|-----------:|------------:|-----------:|")
    rows.forEach { r ->
        println("| ${r.textSize} | ${r.queryCount} | ${r.buildMs} | ${r.searchMs} | ${r.buildMs + r.searchMs} |")
    }
    println()
    println("CSV saved to: ${outputFile.absolutePath}")
}

// ── Helpers ───────────────────────────────────────────────────────

private fun generateText(size: Int): String {
    val chars = "abcdefghijklmnopqrstuvwxyz      \n"
    return buildString(size) {
        repeat(size) { append(chars[Random.nextInt(chars.length)]) }
    }
}

/**
 * Takes random substrings from the text (length 4–10) so queries have real hits.
 * Returns a Set — actual count may be slightly below [target] due to deduplication.
 */
private fun generateQueries(text: String, target: Int): Set<String> {
    val queries = mutableSetOf<String>()
    val maxStart = text.length - 10
    val maxAttempts = target * 3
    var attempts = 0
    while (queries.size < target && attempts < maxAttempts) {
        val start = Random.nextInt(maxStart)
        val len   = Random.nextInt(4, 11)
        queries.add(text.substring(start, start + len))
        attempts++
    }
    return queries
}
