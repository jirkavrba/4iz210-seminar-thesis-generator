package dev.vrba.generator4iz210.service

import dev.vrba.generator4iz210.domain.RegexXmlPattern
import dev.vrba.generator4iz210.service.fulltext.FulltextSearchTaskGenerator
import dev.vrba.generator4iz210.service.fulltext.FulltextSearchTaskOutput
import org.springframework.stereotype.Service

@Service
class ThesisGeneratorService(private val fulltext: FulltextSearchTaskGenerator) {

    fun generateFullText(inputs: List<String>, query: Map<String, List<String>>): FulltextSearchTaskOutput =
        fulltext.generateTaskOutput(inputs, query)

    fun generateExtraction(inputs: List<String>, patterns: List<RegexXmlPattern>): Any =
        TODO("Implement generating the extraction task")
}