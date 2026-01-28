package isis4208

import java.io.File

interface FileSolver {
    fun solve(inputFile: File, outputPath: String?): String
}