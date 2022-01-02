package dev.vrba.generator4iz210.service.fulltext

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FulltextSearchTaskGeneratorTests {

    @Test
    fun testExampleInput() {
        // Taken from https://vse-my.sharepoint.com/:x:/g/personal/klit01_vse_cz/EdNudfDoQYJFtQbBHsN-JeAB3BDmJTyeNf25gr8DLPuPKA?e=HnuaB9
        val inputs = listOf(
            "kachna kachna kachna",
            "jídlo kachna kachna peking",
            "kachna kachna králík recept",
            "králík recept",
            "jídlo kachna peking recept"
        )

        // There is no need to specify variations
        val query = listOf("jídlo", "kachna", "králík", "peking", "recept").associateWith { emptyList<String>() }
        val output = FulltextSearchTaskGenerator().generateTaskOutput(inputs, query)

        assertEquals(
            DocumentTable(
                headers = listOf("Term", "Query", "D1", "D2", "D3", "D4", "D5"),
                rows = listOf(
                    listOf("jídlo",  "1", "0", "1", "0", "0", "1"),
                    listOf("kachna", "1", "3", "2", "2", "0", "1"),
                    listOf("králík", "1", "0", "0", "1", "1", "0"),
                    listOf("peking", "1", "0", "1", "0", "0", "1"),
                    listOf("recept", "1", "0", "0", "1", "1", "1"),
                )
            ),
            output.termFrequency
        )

        assertEquals(
            DocumentTable(
                headers = listOf("Term", "Query", "D1", "D2", "D3", "D4", "D5"),
                rows = listOf(
                    listOf("jídlo",  "1", "0", "0.5", "0", "0", "1"),
                    listOf("kachna", "1", "1", "1", "1", "0", "1"),
                    listOf("králík", "1", "0", "0", "0.5", "1", "0"),
                    listOf("peking", "1", "0", "0.5", "0", "0", "1"),
                    listOf("recept", "1", "0", "0", "0.5", "1", "1"),
                )
            ),
            output.normalisedTermFrequency
        )

        assertEquals(
            DocumentTable(
                headers = listOf("Term", "DF", "IDF"),
                rows = listOf(
                    listOf("jídlo",  "0.4", "1.097"),
                    listOf("kachna", "0.8", "0.796"),
                    listOf("králík", "0.4", "1.097"),
                    listOf("peking", "0.4", "1.097"),
                    listOf("recept", "0.6", "0.921"),
                )
            ),
            output.dfIdf
        )

        assertEquals(
            DocumentTable(
                headers = listOf("Term", "Q", "D1", "D2", "D3", "D4", "D5"),
                rows = listOf(
                    listOf("jídlo",  "1.097", "0", "0.548", "0", "0", "1.097"),
                    listOf("kachna", "0.796", "0.796", "0.796", "0.796", "0", "0.796"),
                    listOf("králík", "1.097", "0", "0", "0.548", "1.097", "0"),
                    listOf("peking", "1.097", "0", "0.548", "0", "0", "1.097"),
                    listOf("recept", "0.921", "0", "0", "0.46", "0.921", "0.921"),
                )
            ),
            output.tfIdf
        )

        assertEquals(
            DocumentTable(
                headers = listOf("Query", "D1", "D2", "D3", "D4", "D5"),
                rows = listOf(
                    listOf("jídlo kachna králík peking recept", "0.353", "0.732", "0.687", "0.635", "0.874")
                )
            ),
            output.similarity
        )
    }
}