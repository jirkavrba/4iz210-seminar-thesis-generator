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
                    listOf("jídlo", "1", "0", "1", "0", "0", "1"),
                    listOf("kachna", "1", "3", "2", "2", "0", "1"),
                    listOf("králík", "1", "0", "0", "1", "1", "0"),
                    listOf("peking", "1", "0", "1", "0", "0", "1"),
                    listOf("recept", "1", "0", "0", "1", "1", "1"),
                )
            ),
            output.termFrequency
        )
    }
}