package dev.vrba.generator4iz210.service

import org.springframework.stereotype.Service
import kotlin.math.log2

@Service
class ThesisGeneratorService {

    fun generateFullText(xname: String, inputs: List<String>, query: Map<String, List<String>>): Any {
        // Split all inputs to separated words without diacritics
        val documents = inputs.map {
            it.replace("[,.;]".toRegex(), " ")
                .split("\\s+".toRegex())
                .filter { word -> word.isNotBlank() }
        }

        fun occurrences(document: List<String>, term: List<String>): Int =
            document.count { it.lowercase() in term.map(String::lowercase) }

        fun tf(document: List<String>, term: List<String>): Double =
            occurrences(document, term).toDouble() / document.size

        fun idf(documents: List<List<String>>, term: List<String>): Double =
            log2(documents.size / documents.sumOf { occurrences(it, term) }.toDouble())

        fun tfidf(document: List<String>, documents: List<List<String>>, term: List<String>): Double =
            tf(document, term) * idf(documents, term)

        return "fulltext"
    }

}