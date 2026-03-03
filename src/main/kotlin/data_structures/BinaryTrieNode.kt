package isis4208.data_structures

data class BinaryTrieNode (
    var char: Char? = null,
    var probability: Double? = null
) {
    var zero: BinaryTrieNode? = null
    var one: BinaryTrieNode? = null
}