package isis4208.tarea_4

import isis4208.FileSolver
import java.io.File
import kotlin.math.*

// =================================================================
//          Algorithm: Shannon-Fano code getter
// =================================================================
/**
 * Actual algorithm
 * In:
 * - frequencies: Map<Char, Double> -> Frequencies of each letter in the message
 * - message: String                -> Represents the message to compress
 * Out: MutableMap<Char, Code>      -> A map of char to its bits representation
 *
 * Explanation:
 * To compress any given text, this algorithm just calculates the alphabet Ψ from the given message.
 * Using the frequencies it calculates the bits required to store each char and the bits representation
 * as UInt with length of actual bits.
 * - Once the bits required per each code are calculated, it starts from the character with the low quantity of bits
 *   and set it as zero filling all bits with zeros as number of bits are fulfilled.
 * - It follows by taking next frequent letter and calculates its code by adding 1 bit (1U) to the previous code
 *   and filling with zeros (shifting left) until complete the number of bits.
 */
fun getShannonFanoCodeDictionary(frequencies: Map<Char, Double>, message: String): MutableMap<Char, Code> {
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
        val frequencies = getFrequencies(message)
        val dictionary = getShannonFanoCodeDictionary(frequencies, message)
        val payload = packPayload(dictionary, message)

        val outputFile = writeCompressed(
            getFilePathWithoutExtension(inputFile),
            inputFile.extension,
            dictionary,
            payload
        )
        return getStats(
            COMPRESSOR.SHANNON_FANO,
            dictionary,
            frequencies,
            payload.bitLen,
            inputFile,
            outputFile
        )
    }
}