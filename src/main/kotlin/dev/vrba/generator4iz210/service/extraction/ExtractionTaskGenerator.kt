package dev.vrba.generator4iz210.service.extraction

import dev.vrba.generator4iz210.domain.RegexXmlPattern
import dev.vrba.generator4iz210.service.XmlFormatter
import org.springframework.stereotype.Component

@Component
class ExtractionTaskGenerator (private val formatter: XmlFormatter) {

    fun generateTaskOutput(inputs: List<String>, patterns: List<RegexXmlPattern>): ExtractionTaskOutput {
        val regex = patterns.joinToString("\n\n") { "${it.regex} - ${it.description}" }
        val products = inputs.mapIndexed { index, input ->
            val name = "input${index + 1}.txt"
            val xml = patterns.joinToString("\n") { extractPattern(input, it) }

            "<SHOPITEM name=\"${name}\">${xml}</SHOPITEM>"
        }
        .joinToString("\n")
        .let { formatter.prettyPrint("<SHOP>$it</SHOP>") }

        return ExtractionTaskOutput(
            regex,
            products
        )
    }

    @Suppress("NAME_SHADOWING")
    private fun extractPattern(input: String, pattern: RegexXmlPattern): String {
        val matches = pattern.regex.toRegex().find(input)?.groupValues ?: listOf()
        val template = pattern.xml.toString()
            .replace("<SHOPITEM>", "")
            .replace("</SHOPITEM>", "")

        return matches.foldIndexed(template) { index, template, match ->
            template.replace("($index)", match)
        }
    }
}