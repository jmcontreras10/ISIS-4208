package isis4208.tarea_5

import isis4208.FileSolver
import isis4208.data_structures.suffix.SuffixArraySearch
import isis4208.data_structures.suffix.SuffixSearch
import isis4208.data_structures.suffix.TrieSearch
import java.io.File

// =================================================================
//                              Algorithm: TextSearch
// =================================================================

/**
 *  Suffix Search Factory method
 *  Receives the type of SuffixSearch and text to initialize it
 */
fun getSuffixSearch(text: String, searchType: SearchTypes): SuffixSearch {
    return when(searchType) {
        SearchTypes.TRIE -> TrieSearch(text)
        SearchTypes.SUFFIX_ARRAY -> SuffixArraySearch(text)
    }
}

// =================================================================
//                              Data types
// =================================================================

enum class SearchTypes {
    TRIE, SUFFIX_ARRAY
}

// =================================================================
//                              Solver
// =================================================================

class TextSearch: FileSolver {
    override fun solve(inputFile: File, outputPath: String?): String {
        val lines = inputFile.readLines()
        require(lines.size >= 2) { "Input file must contain two lines: path to text file and path to queries file" }

        val textFile = File(lines[0].trim())
        val queriesFile = File(lines[1].trim())

        val text = textFile.readText()
        val queries = queriesFile.readLines().filter { it.isNotBlank() }.toSet()

        val searcher = getSuffixSearch(text, SearchTypes.SUFFIX_ARRAY)
        val results = searcher.search(queries)

        val sb = StringBuilder()
        for ((query, positions) in results) {
            sb.append(query)
            positions.sorted().forEach { pos -> sb.append('\t').append(pos) }
            sb.append('\n')
        }
        val output = sb.toString()

        if (outputPath != null) {
            File(outputPath).writeText(output)
        }

        return output
    }
}