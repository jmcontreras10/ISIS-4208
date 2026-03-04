package isis4208.tarea_4

import isis4208.FileSolver
import isis4208.data_structures.BinaryTrieNode

import java.io.File
import java.util.PriorityQueue

// =================================================================
//          Algorithm: Huffman code base compressor
// =================================================================
/**
 * Builds the Huffman code dictionary for a given message.
 *
 * This function:
 * 1. Constructs a Huffman tree using a min-heap (priority queue).
 * 2. Traverses the tree to generate a prefix-free binary code for each character.
 *
 * The resulting dictionary maps each character to its corresponding
 * Huffman [Code], which contains:
 * - The number of bits used.
 * - The binary representation of the code.
 *
 * @param probabilities The probability of each character to be found in the original message.
 * @return A mutable map where each character is associated with its Huffman code.
 *
 */
fun getHuffmanCodeDictionary(probabilities: Map<Char, Double>): MutableMap<Char, Code> {
    //  Getting priority queue of chars by probability
    val minHeap = PriorityQueue(compareBy<BinaryTrieNode> { it.probability })
    probabilities.forEach { (ch, pr) ->
        minHeap.add(BinaryTrieNode(ch, pr))
    }

    // Build the Huffman tree
    val n = probabilities.size
    for (i in 0 until n - 1) {
        val zero = minHeap.poll()
        val one = minHeap.poll()

        val combinedProb  =
            (zero?.probability ?: 0.0) + ((one?.probability ?: 0.0))

        val parent = BinaryTrieNode(null, combinedProb)
        parent.zero = zero
        parent.one = one
        minHeap.add(parent)
    }

    // Root of the Huffman tree
    val root = checkNotNull(minHeap.poll())

    // Traverse the tree to assemble the codes
    val codes = mutableMapOf<Char, Code>()
    getCodes(root, Code(0, 0U), codes)

    return codes
}

/**
 * Recursively traverses the Huffman tree to generate binary codes.
 *
 * Traversal rules:
 * - Going to the left child appends a '0'.
 * - Going to the right child appends a '1'.
 *
 * The function accumulates:
 * - The current number of bits.
 * - The current binary value (stored as an unsigned integer).
 *
 * When a leaf node (node.character != null) is reached,
 * the accumulated code is stored in the dictionary.
 *
 * @param node Current node in the Huffman tree.
 * @param code Accumulated code from the root to this node.
 * @param codes Mutable map where final character codes are stored.
 */
fun getCodes(node: BinaryTrieNode, code: Code, codes: MutableMap<Char, Code>) {
    // Internal node
    if (node.char == null) {
        if (node.zero != null) {
            // Right branch → append 1
            val newCodeValue = if (code.bits == 0) {
                1U
            } else {
                (code.code shl 1) + 1U
            }
            getCodes(
                node.zero!!,
                Code(code.bits + 1, newCodeValue),
                codes
            )
        }
        if (node.one != null) {
            // Left branch → append 0
            val newCodeValue = if (code.bits == 0) {
                0U
            } else {
                code.code shl 1
            }
            getCodes(
                node.one!!,
                Code(code.bits + 1, newCodeValue),
                codes
            )
        }
    } else {
        // Leaf node → store final code
        codes[node.char!!] = code
    }

}

// =================================================================
//                              Data types
// =================================================================

class HuffmanBasedCompressor : FileSolver {
    override fun solve(inputFile: File, outputPath: String?): String {
        val message = inputFile.readText()
        val probabilities = getProbabilities(message)
        val dictionary = getHuffmanCodeDictionary(probabilities)
        val payload = packPayload(dictionary, message)

        val outputFile = writeCompressed(
            getFilePathWithoutExtension(inputFile),
            inputFile.extension,
            dictionary,
            payload,
            CompressorExt.HUFFMAN
        )
        return getStats(
            CompressorName.HUFFMAN,
            dictionary,
            probabilities,
            payload.bitLen,
            inputFile,
            outputFile
        )
    }
}
