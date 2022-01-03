package dev.vrba.generator4iz210.service

import dev.vrba.generator4iz210.service.extraction.ExtractionTaskOutput
import dev.vrba.generator4iz210.service.feed.ProductFeedTaskOutput
import dev.vrba.generator4iz210.service.fulltext.FulltextSearchTaskOutput
import org.apache.poi.xwpf.usermodel.UnderlinePatterns
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileOutputStream

@Component
class ThesisOutputFileWriter {

    fun writeAndCompressThesis(
        xname: String,
        inputs: List<String>,
        fulltext: FulltextSearchTaskOutput,
        extraction: ExtractionTaskOutput,
        feed: ProductFeedTaskOutput
    ): String {
        // Create a temporary directory that will be then compressed
        val directory = Files.createTempDirectory(xname).toFile()

        // Create documents input(n).txt in the output directory
        inputs.mapIndexed { index, content -> File(directory, "input${index + 1}.txt").writeText(content) }

        val word = File(directory, "fulltextvyhledavac.docx")
        val regexp = File(directory, "regexp.txt").writeText(extraction.regex)

        generateFulltextWordDocument(inputs, fulltext).copyTo(word, true)

        return directory.name
    }

    private fun generateFulltextWordDocument(inputs: List<String>, fulltext: FulltextSearchTaskOutput): File {
        val file = File.createTempFile("fulltextvyhledavac", ".docx")
        val out = FileOutputStream(file)

        val document = XWPFDocument()

        // Write all inputs (from input.txt)
        inputs.mapIndexed { index, input ->
            val paragraph = document.createParagraph()
            val header = paragraph.createRun()
            val content = paragraph.createRun()

            header.isBold = true
            header.underline = UnderlinePatterns.SINGLE
            header.setText("Vstupní dokument ${index + 1}:")

            content.isItalic = true
            content.setText(" $input")
            content.addCarriageReturn()
        }

        document.write(out)
        out.close()

        return file
    }

}