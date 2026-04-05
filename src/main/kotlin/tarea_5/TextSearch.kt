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
        TODO("Not yet implemented")
    }

}