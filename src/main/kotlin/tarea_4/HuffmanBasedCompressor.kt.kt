package isis4208.tarea_4

import isis4208.FileSolver

import java.io.File
import java.util.*

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
    //  Getting priority queue chars by frequency
    val minHeap = PriorityQueue<Node>(Comparator.comparing<Node, Double>(Node::freq))
    frequencies.forEach { (ch, d) ->
        minHeap.add(Node(ch, d))
    }

    val n = frequencies.size
    for (i in 0..<n - 1) {
        val left = minHeap.poll()
        val right = minHeap.poll()

        val freq = (left?.freq ?: 0.0) + ((right?.freq ?: 0.0))
        val z = Node(freq, left, right!!)
        minHeap.add(z)
    }

    val tree = checkNotNull(minHeap.poll())

    val codes = mutableMapOf<Char, Code>()
    getCodes(tree, Code(0, 0U), codes)

    return codes
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

fun getCodes(node: Node, code: Code, codes: MutableMap<Char, Code>) {
    if (node.character == null) {
        if (node.right != null) {
            // right Case add 1
            val bitsCode = (code.code shl 1) + 1U
            println("len ${code.bits}")
            println("code ${Integer.toBinaryString(code.code.toInt())}")
            println("bitsCode = ${Integer.toBinaryString(bitsCode.toInt())}")
            getCodes(node.right, Code(code.bits + 1, bitsCode), codes)
        }
        if (node.left != null) {
            // left Case add 0
            val bitsCode = code.code shl 1

            println("len ${code.bits}")
            println("code ${Integer.toBinaryString(code.code.toInt())}")
            println("bitsCode = ${Integer.toBinaryString(bitsCode.toInt())}")

            getCodes(node.left, Code(code.bits + 1, bitsCode), codes)
        }
    } else {
        codes[node.character] = code
    }

}

@JvmRecord
data class Node(
    val character: Char?, val freq: Double, val left: Node?, val right: Node?
) {
    constructor(character: Char, freq: Double) : this(character, freq, null, null)
    constructor(freq: Double, left: Node?, right: Node?) : this(null, freq, left, right)
}
