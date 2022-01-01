package dev.vrba.generator4iz210.service.fulltext

import org.springframework.stereotype.Component
import kotlin.math.log10
import kotlin.math.sqrt

@Component
class FulltextSearchTaskGenerator {

    fun generateTaskOutput(inputs: List<String>, query: Map<String, List<String>>): FulltextSearchTaskOutput {
        // Terms have to be in a deterministic order to be able to produce constant output tables
        val terms = query.keys.toList()
        val documents = inputs
            .map { lemmatiseQueryTerms(it, query) }
            .map { separateWords(it) }


        // Compute term-frequency in all documents
        // Outputs a 2D array with the following indexing: termFrequency[term][document]
        val termFrequency = terms.map { term -> documents.map { occurrences(it, term).toDouble() } }
        val maxOccurrences = termFrequency.reduce { accumulator, current ->
            accumulator.mapIndexed { index, value -> maxOf(value, current[index]) }
        }

        // Normalise the computed term-frequency by diving each cell value with the max of the given document
        val normalisedTermFrequency = termFrequency.map { it.mapIndexed { index, value -> value / maxOccurrences[index] } }

        // Compute document frequencies and inverted document frequencies for each term
        val documentFrequencies = terms.map { term -> documents.count { occurrences(it, term) > 0 } / documents.size.toDouble() }
        val invertedDocumentFrequencies = documentFrequencies.map {
            if (it == 1.0) 0.0 // Special case for division by zero
            else log10(1.0 / it)
        }

        // Compute the final tfidf indexes used for constructing the vector space
        // Again, the indexing is the following: tfidf[term][document]
        val tfidf = normalisedTermFrequency.mapIndexed { index, term -> term.map { it * invertedDocumentFrequencies[index] } }

        // Finally, compute a cosine similarity for each document
        val cosineSimilarity = documents.indices.map { index ->
            val values = tfidf.map { it[index] }

            val numerator = values.zip(invertedDocumentFrequencies).sumOf { (a, b) -> a * b }
            val denominator = sqrt(values.sumOf { it * it }) * sqrt(invertedDocumentFrequencies.sumOf { it * it })

            numerator / denominator
        }

        // D1, D2, ...
        val documentHeaders = documents.indices.map { "D${it + 1}" }

        return FulltextSearchTaskOutput(
            DocumentTable(
                headers = listOf("Term", "Query") + documentHeaders,
                rows = termFrequency.mapIndexed { index, row -> listOf(terms[index], "1") + row.map { it.toInt().toString() } }
            ),
            DocumentTable(listOf(), listOf()),
            DocumentTable(listOf(), listOf()),
            DocumentTable(listOf(), listOf()),
            DocumentTable(listOf(), listOf()),
        )
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