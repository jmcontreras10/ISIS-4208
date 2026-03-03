package isis4208.tarea_4

import isis4208.data_structures.BinaryTrieNode
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File

/**
 * This function retrieves the frequencies for each character in a String message.
 */
fun getFrequencies(message: String): MutableMap<Char, Double> {
    val frequencies = mutableMapOf<Char, Double>()
    val N = message.length

    //  Let's calculate the total
    for (c in message) frequencies[c] = frequencies.getOrDefault(c, 0.0) + 1.0
    for (c in frequencies.keys) frequencies[c] = frequencies[c]!!/N
    return frequencies
}

/**
 * ByteArray with all the bits of the message (compressed) + length of actual useful bits
 */
data class Payload(val bytes: ByteArray, val bitLen: Int)

/**
 * This class initializes with a ByteArray which represents a compressed encoded file
 * and has a single function for:
 * readBit: given the current pointer to bit (byte, bit) returns the bit in that position
 * by masking the required bit and move next
 */
class BitReader(private val bytes: ByteArray) {
    private var byteIndex = 0
    private var bitPos = 0

    fun readBit(): Int {
        val b = bytes[byteIndex].toInt() and 0xFF   // 11111111
        val bit = (b shr (7 - bitPos)) and 1        //00000001
        bitPos++
        if (bitPos == 8) {
            bitPos = 0
            byteIndex++
        }
        return bit
    }
}

/**
 * This class has a set of functions to:
 * 1. writeBit: write a single bit in the current byte
 * 2. flush: save current byte in the list and start writing in a new one (specially when a byte is full)
 * 3. writeCode: write all bits in a code into the current byte (by using writeBit)
 * 4. toPayload: transform the final byte array into payload object (byteArray, bits length)
 */
class BitWriter {
    private val out = ArrayList<Byte>(1024)
    private var currentByte = 0
    private var currentBitPos = 0
    private var bitLen = 0

    private fun writeBit(bit: Int) {
        currentByte = currentByte or (bit shl (7 - currentBitPos))
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
 * This function packs all the message codes (1s and 0s) into a compressed payload (ByteArray).
 */
fun packPayload(codes: Map<Char, Code>, message: String): Payload {
    val writer = BitWriter()
    for (c in message) writer.writeCode(codes[c]!!)
    return writer.toPayload()
}

/**
 *  This function writes the dictionary and Payload bytes into a given file
 */
fun writeCompressed(
    inputFile: File,
    codes: Map<Char, Code>,
    message: String,
    compressor: String,
): String {
    //  Saving original extension
    val extension = inputFile.extension
    //  Getting the input file name for creating the compressed one with .matt extension
    var outPath = inputFile.absolutePath
    outPath = if (extension.isNotEmpty()) {
        outPath.substringBeforeLast("." + inputFile.extension)
    } else {
        outPath
    } + ".matt"
    val outFile = File(outPath)

    DataOutputStream(BufferedOutputStream(outFile.outputStream())).use { out ->
        //  Saving extension
        out.writeShort(extension.length)    //  Len of extension name
        for (c in extension) {
            out.writeShort(c.code)          //  Write each letter on extension
        }

        //  Dictionary
        out.writeShort(codes.size)
        for ((c, code) in codes.entries.sortedBy { it.key.code }) {
            out.writeShort(c.code)
            out.writeByte(code.bits)
            out.writeInt(code.code.toInt())
        }

        //  Payload
        val payload = packPayload(codes, message)
        out.writeInt(payload.bitLen)
        out.writeInt(payload.bytes.size)
        out.write(payload.bytes)
        out.flush()
    }

    val compressionRatio = inputFile.length() * 1f / outFile.length()
    println("File compressed successfully at: $outPath.")
    println("Compression ratio: $compressionRatio")

    val dict = StringBuilder("The $compressor codes for the input message are:\n")
    dict.append("{\n")
    for ((c, code) in codes.entries) dict.append("     $c -> ${code}, ${Integer.toBinaryString(code.code.toInt())}\n")
    dict.append("}")
    return dict.toString()
}

/**
 * This function reads a compressed file for:
 * 1. The original file extension
 * 2. The dictionary to decompress the encoded message
 * 3. The bits (byte to byte) of the compressed message and decodes it into a readable message string
 *    by using the dictionary.
 */
fun readCompressed(
    file: File,
): Pair<String, String> {
    //  Since reading is bit to bit, a trie is a better solution than an actual dictionary
    val root = BinaryTrieNode()

    //  This pushes a char down the trie following bits that forms it
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

        //  Dictionary
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

        //  Recursive decoding
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



