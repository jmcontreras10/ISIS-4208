package isis4208.tarea_4

import isis4208.data_structures.BinaryTrieNode
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import kotlin.math.log2

// =================================================================
//          General Entropy Codes generation Utilities
// =================================================================

/**
 * This function process receives a String message as input, calculates each character frequency in the message
 * and returns a map of Char to probability.
 */
fun getProbabilities(message: String): Map<Char, Double> {
    val probabilities = mutableMapOf<Char, Double>()
    val N = message.length

    //  Let's calculate the total frequencies
    for (c in message) probabilities[c] = probabilities.getOrDefault(c, 0.0) + 1.0
    //  Let's calculate each probability
    for (c in probabilities.keys) probabilities[c] = probabilities[c]!!/N
    return probabilities
}

// =================================================================
//                      Data Classes
// =================================================================

/**
 * This is a useful structure to pack all the bytes required to be written into a file.
 * Is composed of a ByteArray representing the bytes to be written and the length of actual compressed bits.
 */
data class Payload(val bytes: ByteArray, val bitLen: Int)

/**
 * Compressors name enum
 */
enum class CompressorName(val compressorName: String) {
    SHANNON_FANO("Shannon-Fano"),
    HUFFMAN("Huffman")
}

/**
 * Compressors extension enum
 */
enum class CompressorExt(val compressorExt: String) {
    SHANNON_FANO("sf"),
    HUFFMAN("hf")
}

// =================================================================
//                  Bit level utilities
// =================================================================
/**
 * This class is a utility for reading all bits from a byteArray. It initializes with a ByteArray which represents
 * a compressed encoded file bits and has a single function for:
 * readBit: given the current pointer to a bit (byteIndex, bitPos) returns the bit in that
 * position of the compressed message by masking the required bit. Finally, iterates to the next bit.
 */
class BitReader(private val bytes: ByteArray) {
    private var byteIndex = 0
    private var bitPos = 0

    fun readBit(): Int {
        val byte = bytes[byteIndex].toInt() and 0xFF    //  Getting only the last 8 bits since Int has 32 (no sign)
        val bit = (byte shr (7 - bitPos)) and 1         //  Masking with 0... 00000001 to get last bit (bitPos)
        bitPos++                                        //  Iterator.next()
        if (bitPos == 8) {                              //  In case bitPos reached the last bit of byte
            bitPos = 0
            byteIndex++
        }
        return bit
    }
}

/**
 * This class is a utility for writing a set of bits in a byteArray. It has a set of functions to:
 * 1. writeBit: write a single bit in the current byte (buffer) at index position (currentByte, currentBitPos)
 *            Then adds one more bit in the total bit count and triggers flush if byte is full.
 * 2. flush: adds the current byte (buffer) to the byteArray once a byte was completely written or all codes
 *           were totally written. Then reset the byte to keep writing.
 * 3. writeCode: write all bits of a given code using many bytes (buffers) as required
 * 4. toPayload: transform the final byte array into payload object (byteArray, bits length)
 */
class BitWriter {
    private val out = ArrayList<Byte>(1024)
    private var currentByte = 0
    private var currentBitPos = 0
    private var bitLen = 0

    private fun writeBit(bit: Int) {
        currentByte = currentByte or (bit shl (7 - currentBitPos))  //  moving the bit of interest to current bit index
        currentBitPos++
        bitLen++
        if (currentBitPos == 8) flush()
    }

    private fun flush() {
        out.add(currentByte.toByte())
        currentByte = 0
        currentBitPos = 0
    }

    fun writeCode(code: Code) {
        for (i in code.bits - 1 downTo 0) {
            val bit = ((code.code shr i) and 1U).toInt()    //  00000001
            writeBit(bit)
        }
    }

    fun toPayload(): Payload {
        if (currentBitPos != 0) flush()
        return Payload(out.toByteArray(), bitLen)
    }
}


/**
 * This function uses the BitWriter to process each character in the message, transform it to
 * the corresponding code and pack it into a ByteArray
 */
fun packPayload(codes: Map<Char, Code>, message: String): Payload {
    val writer = BitWriter()
    for (c in message) writer.writeCode(codes[c]!!)
    return writer.toPayload()
}

// =================================================================
//                      I/O Utilities
// =================================================================

/**
 * This utility retrieves the full path of a given file and returns it without extension.
 */
fun getFilePathWithoutExtension(file: File): String {
    val extension = file.extension
    var path = file.absolutePath
    path = if (extension.isNotEmpty()) {
        path.substringBeforeLast("." + file.extension)
    } else {
        path
    }
    return path
}

/**
 *  This handles already encoded message (as Payload: byteArray + length) writing, and returning it.
 *  This utility receives the output file path to write in and data to write:
 *  - its original extension name
 *  - the encoding dictionary
 *  - the payload (actual bits)
 */
fun writeCompressed(
    outPath: String,
    extensionName: String,
    codesDict: Map<Char, Code>,
    payload: Payload,
    compressor: CompressorExt
): File {
    val outFile = File("$outPath.${compressor.compressorExt}")
    outFile.parentFile?.mkdirs()    //  Safe in case dir does not exist

    DataOutputStream(BufferedOutputStream(outFile.outputStream())).use { out ->
        //  Extension
        out.writeShort(extensionName.length)    //  Length of extension name
        for (c in extensionName) {
            out.writeShort(c.code)              //  Write each letter on extension
        }

        //  Dictionary
        out.writeShort(codesDict.size)              //  Size of the dictionary
        for ((c, code) in codesDict.entries.sortedBy { it.key.code }) {
            out.writeShort(c.code)              //  Character
            out.writeByte(code.bits)            //  Number of bits
            out.writeInt(code.code.toInt())     //  bits
        }

        //  Payload
        out.writeInt(payload.bitLen)            //  actual bits length
        out.writeInt(payload.bytes.size)        //  number of bytes (for easy read)
        out.write(payload.bytes)                //  actual bits
        out.flush()
    }
    println("File compressed successfully at: $outPath.")
    return outFile
}

/**
 * This handles already encoded file reading, and returns the uncompressed message + its original extension.
 * By receiving the input file only, this reads:
 * - original extension
 * - dictionary to decode
 * - encoded message
 * Then uses the dictionary as trie representation and decodes the message.
 */
fun readCompressed(
    file: File,
): Pair<String, String> {
    //  Since reading is bit to bit, a trie is a better solution than a Map
    val root = BinaryTrieNode()

    //  Pre-processing function (Trie construction)
    fun insertCode(ch: Char, bits: Int, code: Int) {
        var node = root
        for (i in bits - 1 downTo 0) {
            val bit = (code shr i) and 1
            node = if (bit == 0) {
                node.zero ?: BinaryTrieNode().also { node.zero = it }
            } else {
                node.one ?: BinaryTrieNode().also { node.one = it }
            }
        }
        node.char = ch
    }

    DataInputStream(BufferedInputStream(file.inputStream())).use { input ->
        //  Extension
        val extensionLen = input.readUnsignedShort()
        val extensionBuilder = StringBuilder()
        for (i in 0 until extensionLen) {
            extensionBuilder.append(input.readUnsignedShort().toChar())
        }
        val extension = extensionBuilder.toString()

        //  Dictionary - pre-processing
        val dictSize = input.readUnsignedShort()
        for (i in 0 until dictSize) {
            val charCode = input.readUnsignedShort()
            val bits = input.readUnsignedByte()
            val code = input.readInt()
            insertCode(charCode.toChar(), bits, code)
        }

        //  Payload
        val bitLen = input.readInt()
        val byteLen = input.readInt()
        val payloadBytes = ByteArray(byteLen)
        input.readFully(payloadBytes)

        val reader = BitReader(payloadBytes)
        val text = StringBuilder()
        var node = root

        //  Recursive decoding using the trie
        //  No ending character needed since there are no repeated suffixes
        for(i in 0 until bitLen) {
            val bit = reader.readBit()
            node = if (bit == 0) node.zero ?: error("Invalid bitstream (no matching code)")
                else node.one ?: error("Invalid bitstream (no matching code)")

            val char = node.char
            if (char != null) {
                text.append(char)
                node = root
            }
        }

        return Pair(text.toString(), extension)
    }
}

// =================================================================
//                      Stats and result
// =================================================================

/**
 *  This prints the final expected result after running a compression algorithm.
 *  In:
 *  - compressor: compressor name
 *  - codesDict: to print the used dictionary
 *  - probabilities: to calculate expected number of bits of the payload, the entropy in the worst case
 *  - payloadLen: to print it as stat
 *  - inputFile and outputFile: to calculate and print compression ratio
 */
fun getStats(
    compressor: CompressorName,
    codesDict: Map<Char, Code>,
    probabilities: Map<Char, Double>,
    payloadLen: Int,
    inputFile: File,
    outputFile: File
): String {
    //  ⚠️ This function was generated with generative AI
    fun printableChar(c: Char): String = when (c) {
        '\n' -> "\\n"
        '\r' -> "\\r"
        '\t' -> "\\t"
        ' '  -> "' '"
        else -> c.toString()
    }

    val ans = StringBuilder()
    //  Dictionary
    ans.append("The ${compressor.compressorName} codes for the input message are:\n")
    ans.append("{\n")

    for ((c, code) in codesDict.entries) {
        val bits = Integer.toBinaryString(code.code.toInt()).padStart(code.bits, '0')
        ans.append("     ${printableChar(c)} -> $code, $bits\n")
    }
    ans.append("}\n\n")

    //  Stats
    ans.append("#################### Stats for ${compressor.compressorName} codes calculation ####################\n\n")
    val expectedBits = codesDict.entries.sumOf { (char, code) ->
        code.bits * probabilities[char]!!
    }
    ans.append("Expected number of bits: (Σ μ ∈ Ψ |: Pr(μ)*(|B(μ)|)) = $expectedBits.\n")

    val worstCaseEntropy = log2(codesDict.size * 1.0)
    ans.append("Worst case entropy: log2(|Ψ|) = $worstCaseEntropy.\n")

    ans.append("Actual number of bits of payload after compression = $payloadLen.\n")

    val compressionRatio = inputFile.length() * 1f / outputFile.length()
    ans.append("Compression ratio: $compressionRatio.\n\n")

    ans.append("##################################################################################\n")
    return ans.toString()
}


