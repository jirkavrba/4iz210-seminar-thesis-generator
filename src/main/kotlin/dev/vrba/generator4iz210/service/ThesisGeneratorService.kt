package dev.vrba.generator4iz210.service

import org.springframework.stereotype.Service

@Service
class ThesisGeneratorService {

    fun generateFullText(xname: String, inputs: List<String>, query: List<String>, frequencies: List<List<Int>>): Any {
        // Frequencies are mapped like this:
        // frequencies[A][B] -> the number of occurrences of term query[A] in inputs[B]
        if (frequencies.size != query.size || frequencies.any {  it.size != inputs.size }) {
            throw IllegalArgumentException("Document frequencies shape doesn't match inputs and query lengths!")
        }

        return "fulltext"
    }

}