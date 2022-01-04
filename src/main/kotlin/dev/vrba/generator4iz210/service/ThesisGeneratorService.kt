package dev.vrba.generator4iz210.service

import dev.vrba.generator4iz210.domain.RegexXmlPattern
import dev.vrba.generator4iz210.service.extraction.ExtractionTaskGenerator
import dev.vrba.generator4iz210.service.extraction.ExtractionTaskOutput
import dev.vrba.generator4iz210.service.feed.ProductFeedTaskGenerator
import dev.vrba.generator4iz210.service.feed.ProductFeedTaskOutput
import dev.vrba.generator4iz210.service.fulltext.FulltextSearchTaskGenerator
import dev.vrba.generator4iz210.service.fulltext.FulltextSearchTaskOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File

@Service
class ThesisGeneratorService(
    private val fulltext: FulltextSearchTaskGenerator,
    private val extraction: ExtractionTaskGenerator,
    private val feed: ProductFeedTaskGenerator,
    private val writer: ThesisOutputFileWriter,
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.qualifiedName)

    fun generateThesis(xname: String, inputs: List<String>, query: Map<String, List<String>>, patterns: List<RegexXmlPattern>): String  {
        val fulltext = generateFullText(inputs, query)
        val extraction = generateExtraction(inputs, patterns)
        val feed = generateProductFeed(extraction.products, patterns)

        // Compress everything into a single zip file
        val file = writer.writeAndCompressThesis(xname, inputs, fulltext, extraction, feed)

        logger.info("Generated thesis for $xname with output in ${file.absolutePath}")

        // And upload the file to https://transfer.sh
        return uploadFile(file)
    }

    private fun generateFullText(inputs: List<String>, query: Map<String, List<String>>): FulltextSearchTaskOutput =
        fulltext.generateTaskOutput(inputs, query)

    private fun generateExtraction(inputs: List<String>, patterns: List<RegexXmlPattern>): ExtractionTaskOutput =
        extraction.generateTaskOutput(inputs, patterns)

    private fun generateProductFeed(xml: String, patterns: List<RegexXmlPattern>): ProductFeedTaskOutput =
        feed.generateTaskOutput(xml, patterns)

    private fun uploadFile(file: File): String {
        // Run the curl command as instructed on the website
        // curl --upload-file ./hello.txt https://transfer.sh/hello.txt
        val builder = ProcessBuilder().command("curl", "--upload-file", file.absolutePath, "https://transfer.sh/${file.name}")
        val process = builder.start()
        val link = process.inputReader().readLine()

        logger.info("Uploaded ${file.absolutePath} to $link")

        process.destroy()

        return link
    }
}