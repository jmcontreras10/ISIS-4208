package isis4208.tarea_4

import isis4208.FileSolver
import java.io.File
import kotlin.math.*

// =================================================================
//          Algorithm: Shannon-Fano code getter
// =================================================================
/**
 * Actual algorithm
 * In: String                       -> Represents the message to compress
 * Out: MutableMap<Char, Code>      -> A map of char to its bits representation
 *
 * Explanation:
 * To compress any given text, this algorithm just calculates the alphabet Ψ from the given message.
 * Then in a first loop it calculates the probability distribution per each μ in Ψ (frequencies).
 * Using the frequencies it calculates the bits required to store each char and the bits representation
 * as UInt with length of actual bits
 */
fun getShannonFanoCodeDictionary(message: String): MutableMap<Char, Code> {
    //  Getting frequencies
    val frequencies = getFrequencies(message)
    //  Getting sorted chars by frequency in descending order
    val sortedCharsPerFrequency = frequencies
        .toList()
        .sortedByDescending { it.second }
        .map { it.first }

    //  Getting # of bits needed for encode based on probabilities and Shannon's entropy formula:
    //  roof(log_2(1/Pr(μ))), with μ in Ψ
    val bits = mutableMapOf<Char, Int>()
    for (c in sortedCharsPerFrequency) bits[c] = ceil(
        log2(1 / frequencies[c]!!)
    ).toInt()
    val mostFrequentChar = sortedCharsPerFrequency[0]

    //  Now getting the actual codes of Shannon-Fano
    val codes = mutableMapOf<Char, Code>()
    codes[mostFrequentChar] = Code(bits[mostFrequentChar]!!, 0U)
    for (i in 1 until sortedCharsPerFrequency.size) {
        val previousChar = sortedCharsPerFrequency[i-1]
        val currentChar = sortedCharsPerFrequency[i]

        val bitsDifference = bits[currentChar]!! - bits[previousChar]!!
        val code = (codes[previousChar]!!.code + 1U) shl bitsDifference
        codes[currentChar] = Code(bits[currentChar]!!, code)
    }
    return codes
}

// =================================================================
//                              Data types
// =================================================================
data class Code(val bits: Int, val code: UInt)

class ShannonFanoBasedCompressor: FileSolver {

    override fun solve(inputFile: File, outputPath: String?): String {
        val message = inputFile.readText()
        val dictionary = getShannonFanoCodeDictionary(message)
        return writeCompressed(
            inputFile,
            dictionary,
            message,
            "Shannon-Fano"
        )
    }
}