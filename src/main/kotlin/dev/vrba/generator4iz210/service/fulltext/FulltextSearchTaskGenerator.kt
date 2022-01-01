package dev.vrba.generator4iz210.service.fulltext

import org.springframework.stereotype.Component

@Component
class FulltextSearchTaskGenerator {

    fun generateTaskOutput(inputs: List<String>, query: Map<String, List<String>>): FulltextSearchTaskOutput {
        // Terms have to be in a deterministic order to be able to produce constant output tables
        val terms = query.keys.toList()
        val documents = inputs
            .map { lemmatiseQueryTerms(it, query) }
            .map { separateWords(it) }


        // Compute term-frequency in all documents
        val termFrequency = terms.map { term -> documents.map { occurrences(it, term) } }

        // Normalise the computed term-frequency by diving each cell value with the max of the given document
//        val normalisedTermFrequency = ...
        TODO("Implement task solution generator")
    }

    private fun occurrences(document: List<String>, term: String): Int =
        document.count { it.lowercase() == term.lowercase() }

    // Replace all query variations with the base term (a substitution for proper lemmatisation)
    private fun lemmatiseQueryTerms(input: String, query: Map<String, List<String>>): String =
        query.entries.fold(input) { document, (term, variations) ->
            variations.fold(document) { text, variation -> text.replace(variation, term) }
        }

    // Split all inputs to separated words without diacritics
    private fun separateWords(input: String): List<String> =
        input.replace("[,.;]".toRegex(), " ")
            .split("\\s+".toRegex())
            .filter { word -> word.isNotBlank() }
}