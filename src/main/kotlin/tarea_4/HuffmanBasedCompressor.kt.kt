package isis4208.tarea_4

import isis4208.FileSolver
import isis4208.data_structures.BinaryTrieNode
import java.io.File

// =================================================================
//          Algorithm: Huffman code base compressor
// =================================================================
/**
 * Actual algorithm
 * In: String                       -> Represents the message to compress
 * Out: MutableMap<Char, Code>      -> A map of char to its bits representation
 */
fun getHuffmanCodeDictionary(message: String): MutableMap<Char, Code> {
    //  Getting frequencies
    val frequencies = getFrequencies(message)
    //  Getting sorted chars by frequency
    val charsQueue = frequencies
        .toList()
        .sortedBy { it.second }
        .toMutableList()

    //  Building the Trie
    var root = BinaryTrieNode()

    fun insertChar() {
        if (root.one == null && root.zero == null) {
            val (charA, probA) = charsQueue.removeFirst()
            val (charB, probB) = charsQueue.removeFirst()
            root.one = BinaryTrieNode(charA, probA)
            root.one = BinaryTrieNode(charB, probB)
        }
        var node = root

    }
    return mutableMapOf()
}

class HuffmanBasedCompressor: FileSolver {
    override fun solve(inputFile: File, outputPath: String?): String {
        TODO("Not yet implemented")
    }
}