package isis4208.tarea_4

import isis4208.FileSolver
import java.io.File

class Decompressor: FileSolver {

    /**
     * Small helper function for get the input (compressed file)
     * And create a decompressed file with the same name but .txt extension
     */
    private fun writeFile(inputFile: File, message: String, extension: String): String {
        var outPath = inputFile.absolutePath
        if (inputFile.extension != "matt")
            error("The file extension should be 'matt', but was '${inputFile.extension}' instead.")

        outPath = if (inputFile.extension.isNotEmpty()) {
            outPath.substringBeforeLast("." + inputFile.extension)
        } else {
            outPath
        } + "_decompressed.$extension"
        val outFile = File(outPath)
        outFile.writeText(message)
        return "File decompressed successfully at: $outPath."
    }

    override fun solve(inputFile: File, outputPath: String?): String {
        val (message, extension) = readCompressed(inputFile)
        return writeFile(inputFile, message, extension)
    }
}