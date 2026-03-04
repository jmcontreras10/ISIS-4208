package isis4208.tarea_4

import isis4208.FileSolver
import java.io.File

class Decompressor: FileSolver {

    /**
     * Small helper function for get the input (compressed file)
     * And create a decompressed file with the same name but .txt extension
     */
    private fun writeFile(inputFile: File, message: String, extension: String): String {
        if (CompressorExt.entries.toTypedArray().map{ it.compressorExt }.contains(inputFile.extension))
            error("The file extension should be 'sf' ot 'hf, but was '${inputFile.extension}' instead.")

        val outPath = "${getFilePathWithoutExtension(inputFile)}_decompressed.$extension"
        val outFile = File(outPath)
        outFile.parentFile?.mkdirs()    //  Safe in case dir does not exist

        outFile.writeText(message)
        return "File decompressed successfully at: $outPath."
    }

    override fun solve(inputFile: File, outputPath: String?): String {
        val (message, extension) = readCompressed(inputFile)
        return writeFile(inputFile, message, extension)
    }
}