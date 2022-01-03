package dev.vrba.generator4iz210.service

import dev.vrba.generator4iz210.service.extraction.ExtractionTaskOutput
import dev.vrba.generator4iz210.service.feed.ProductFeedTaskOutput
import dev.vrba.generator4iz210.service.fulltext.FulltextSearchTaskOutput
import org.apache.poi.xwpf.usermodel.ParagraphAlignment
import org.apache.poi.xwpf.usermodel.UnderlinePatterns
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

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

        val word = generateFulltextWordDocument(inputs, fulltext)

        File(directory, "regexp.txt").writeText(extraction.regex)
        File(directory, "products.xml").writeText(extraction.products)
        File(directory, "products.xsd").writeText(feed.schema)
        File(directory, "products.json").writeText(feed.json)
        File(directory, "products.csv").writeText(feed.csv)

        word.copyTo(File(directory, "fulltextvyhledavac.docx"), true)

        val zip = File.createTempFile("${xname}_", ".zip")
        val file = FileOutputStream(zip)
        val output = ZipOutputStream(file)

        // Zip everything from the temporary directory to the output zip file
        directory.listFiles()?.forEach {
            val input = FileInputStream(it)
            val entry = ZipEntry(it.name)

            output.putNextEntry(entry)
            output.write(input.readAllBytes())

            input.close()
        }

        output.close()
        file.close()

        // Finally, delete the temporary directory
        directory.delete()

        return zip.absolutePath
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

        // Write down the query
        val query = document.createParagraph()
        val queryHeader = query.createRun()
        val queryContent = query.createRun()

        queryHeader.isBold = true
        queryHeader.underline = UnderlinePatterns.SINGLE
        queryHeader.setText("Dotaz:")

        queryContent.setText(" ${fulltext.query}")
        queryContent.addCarriageReturn()

        // And finally, print out the result tables
        val results = document.createParagraph()
        val resultsHeader = results.createRun()
        val resultsContent = results.createRun()

        resultsHeader.isBold = true
        resultsHeader.underline = UnderlinePatterns.SINGLE
        resultsHeader.setText("Řešení:")

        val tables = mapOf(
            "Tabulka 1: TF" to fulltext.termFrequency,
            "Tabulka 2: TF – normalizovaná" to fulltext.normalisedTermFrequency,
            "Tabulka 3: DF, IDF" to fulltext.dfIdf,
            "Tabulka 4: TF-IDF" to fulltext.tfIdf,
            "Tabulka 5: podobnost dokument - dotaz" to fulltext.similarity
        )

        tables.forEach { (label, table) ->
            document.createParagraph()

            val container = document.createTable()
            val headerRow = container.createRow()

            container.removeRow(0)
            headerRow.removeCell(0)
            table.headers.forEach {
                headerRow.createCell().apply {
                    text = it

                    paragraphs[0].alignment = ParagraphAlignment.CENTER
                    paragraphs[0].runs[0].isBold = true
                }
            }

            table.rows.forEach { item ->
                val row = container.createRow()
                item.forEachIndexed { index, content -> row.getCell(index).apply { text = content } }
            }

            val bottom = document.createParagraph()
            val run = bottom.createRun()

            run.isItalic = true
            run.setText(label)
        }

        document.write(out)
        out.close()

        return file
    }

}