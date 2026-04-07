package isis4208.data_structures.suffix

/**
 * Since there are many suffix data structures to implement the search problem like Tries and suffix arrays,
 * this interface defines the common methods for these data structures to implement them as needed.
 */
abstract class SuffixSearch(protected val text: String) {

    /**
     * For a given set of queries, this method uses the internal data structure to search for the position indices
     * where the given words are found
     * In:
     * - queries: Set<String>   -> set of queries
     */
    abstract fun search(queries: Set<String>): Map<String, Set<Int>>
}