package dev.vrba.generator4iz210.service.fulltext

import org.springframework.stereotype.Component

@Component
class FulltextSearchTaskGenerator {

    fun generateTaskOutput(inputs: List<String>, query: Map<String, List<String>>): FulltextSearchTaskOutput {
        // Split all inputs to separated words without diacritics
        val documents = inputs.map {
            it.replace("[,.;]".toRegex(), " ")
                .split("\\s+".toRegex())
                .filter { word -> word.isNotBlank() }
        }

        TODO("Implement task solution generator")
    }

    private fun occurrences(document: List<String>, term: List<String>): Int =
        document.count { it.lowercase() in term.map(String::lowercase) }
}