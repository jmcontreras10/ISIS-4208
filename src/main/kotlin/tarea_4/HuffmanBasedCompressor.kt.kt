package isis4208.tarea_4

import isis4208.FileSolver

import java.io.File
import java.util.*

// =================================================================
//          Algorithm: Huffman code base compressor
// =================================================================
/**
 * Builds the Huffman code dictionary for a given message.
 *
 * This function:
 * 1. Computes the frequency of each character in the input message.
 * 2. Constructs a Huffman tree using a min-heap (priority queue).
 * 3. Traverses the tree to generate a prefix-free binary code for each character.
 *
 * The resulting dictionary maps each character to its corresponding
 * Huffman [Code], which contains:
 * - The number of bits used.
 * - The binary representation of the code.
 *
 * @param message The input text to compress.
 * @return A mutable map where each character is associated with its Huffman code.
 *
 */
fun getHuffmanCodeDictionary(message: String): MutableMap<Char, Code> {
    //  Getting frequencies
    val frequencies = getFrequencies(message)
    //  Getting priority queue of chars by frequency
    val minHeap = PriorityQueue<Node>(
        Comparator.comparing<Node, Double>(Node::freq)
    )
    frequencies.forEach { (ch, d) ->
        minHeap.add(Node(ch, d))
    }

    // Build the Huffman tree
    val n = frequencies.size
    for (i in 0 until n - 1) {
        val left = minHeap.poll()
        val right = minHeap.poll()

        val combinedFreq  =
            (left?.freq ?: 0.0) + ((right?.freq ?: 0.0))

        val parent = Node(combinedFreq, left, right)
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
fun getCodes(node: Node, code: Code, codes: MutableMap<Char, Code>) {
    // Internal node
    if (node.character == null) {
        if (node.right != null) {
            // Right branch → append 1
            val newCodeValue = if (code.bits == 0) {
                1U
            } else {
                (code.code shl 1) + 1U
            }
            getCodes(
                node.right,
                Code(code.bits + 1, newCodeValue),
                codes
            )
        }
        if (node.left != null) {
            // Left branch → append 0
            val newCodeValue = if (code.bits == 0) {
                0U
            } else {
                code.code shl 1
            }
            getCodes(
                node.left,
                Code(code.bits + 1, newCodeValue),
                codes
            )
        }
    } else {
        // Leaf node → store final code
        codes[node.character] = code
    }

}

// =================================================================
//                              Data types
// =================================================================
/**
 * Represents a node in the Huffman tree.
 *
 * A node can be:
 * - A leaf node: contains a character and its frequency.
 * - An internal node: contains no character and has two children.
 *
 * @property character The character stored in this node (null for internal nodes).
 * @property freq The frequency of the character or the sum of frequencies of its subtree.
 * @property left Left child (represents appending 0 in the code).
 * @property right Right child (represents appending 1 in the code).
 */
@JvmRecord
data class Node(
    val character: Char?, val freq: Double, val left: Node?, val right: Node?
) {
    constructor(character: Char, freq: Double) : this(character, freq, null, null)
    constructor(freq: Double, left: Node?, right: Node?) : this(null, freq, left, right)
}

class HuffmanBasedCompressor : FileSolver {
    override fun solve(inputFile: File, outputPath: String?): String {
        val message = inputFile.readText()
        val dictionary = getHuffmanCodeDictionary(message)
        return writeCompressed(
            inputFile,
            dictionary,
            message,
            "Huffman"
        )
    }
}
