package tarea_1

import isis4208.FileSolver
import java.util.LinkedList
import java.util.Queue
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// =================================================================
//                          Algorithm
// =================================================================

/**
 * Actual algorithm
 * In: List<IntArray>   -> Representation of the users' graph as adjacency lists. G(V, E), V = User, E = VxV
 * Out: Boolean         -> Whether the given network satisfies the six degrees of separation.
 *
 * Explanation:
 * We run a BFS from each user (N BFS runs total). Each BFS explores the friendship graph
 * level by level . We stop expanding after 6 levels because we only care about paths of length ≤ 6.
 *
 * If, after exploring up to depth 6 from any starting user, not all users were reached,
 * then some pair of users is more than six connections apart, so we return false.
 * Otherwise, if every BFS reaches all users within 6 steps, we return true.
 */
fun sixDegreeTheory (users: Array<IntArray>): Boolean {
    val n = users.size

    fun bfsWithin6 (rootUser: Int): Boolean {
        val queue: Queue<Int> = LinkedList()
        val visited = BooleanArray(n){false}
        var visitedCount = 1
        var depth = 0

        queue.add(rootUser)
        visited[rootUser] = true

        while (!queue.isEmpty() && depth < 6) {
            val levelSize = queue.size
            repeat (levelSize) {
                val currentUser = queue.poll()
                for (friend in users[currentUser]) {
                    if(!visited[friend]) {
                        visited[friend] = true
                        visitedCount++
                        if (visitedCount == n) return true
                        queue.add(friend)
                    }
                }
            }
            depth++
        }
        return visitedCount == n
    }

    for (userIndex in users.indices) {
        if (!bfsWithin6(userIndex)) return false
    }
    return true
}

// =================================================================
//                       Parse Functions
//  =================================================================

/**
 * Useful function to parse User data to simplified version of a graph.
 * From list of User objects to simply array of numbers array.
 * Mapping user id to its index in the array.
 */
    fun parseGraph(data: List<User>): Array<IntArray> {
    val idToIndex: Map<Int, Int> = data
        .withIndex()
        .associate { it.value.id to it.index }

    val adjacency: Array<IntArray> = data.map {
            user ->
        user.friends.map {
                friend: Int ->
            idToIndex[friend]?: error("Friend id ${friend} not found")
        }.toIntArray()
    }.toTypedArray()

    return adjacency
}

// =================================================================
//                              Data types
// =================================================================

/**
 * Simulated payload of users coming from Facebook
 */
@Serializable
private data class UsersPayload(
    val users: List<User>
)

/**
 * Representation of a user coming from "API" of Facebook.
 * id: Symbolic unique id that identifies a User in the platform
 * friends: Simple representation of linked friends' ids of the user
 */
@Serializable
data class User (
    val id: Int,
    val friends: IntArray
)

private val json = Json {
    ignoreUnknownKeys = true
}

// =================================================================
//                  Problem Solver
// =================================================================

class Problem1SixDegreesSolver : FileSolver {
    /**
     * Function for read the json file
     */
    private fun readUsersFromJsonFile(file: File): List<User> {
        val text = file.readText(Charsets.UTF_8)
        val payload = json.decodeFromString<UsersPayload>(text)
        return payload.users
    }

    override fun solve(inputFile: File): String {
        val users = readUsersFromJsonFile(inputFile)
        val ok = sixDegreeTheory(parseGraph(users))
        return if (ok) "true" else "false"
    }
}