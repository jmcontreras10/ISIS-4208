package isis4208

import java.io.File

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        if (args.size != 3 || args[0] in setOf("-h", "--help")) {
            printUsage()
            return
        }

        val assignment = args[0]
        val problem = args[1]
        val inputPath = args[2]

        val solver = Registry.get(assignment, problem)
        if (solver == null) {
            System.err.println("Unknown assignment/problem: assignment='$assignment', problem='$problem'")
            System.err.println("Available: ${Registry.available()}")
            printUsage()
            return
        }

        val file = File(inputPath)
        if (!file.exists() || !file.isFile) {
            System.err.println("Input file not found: $inputPath")
            return
        }

        try {
            val output = solver.solve(file)
            print(output)
            if (!output.endsWith("\n")) println()
        } catch (e: Exception) {
            System.err.println("Error: ${e.message}")
        }
    }

    private fun printUsage() {
        System.err.println("Usage: java -jar isis4208.jar <assignment> <problem> <input_path>")
        System.err.println("  <assignment> e.g. Tarea_1, Tarea_2, ...")
        System.err.println("  <problem>    e.g. 1,2,3,4,5")
        System.err.println("Example:")
        System.err.println("  java -jar dsa.jar Tarea_1 1 inputs/p1.json")
    }
}
