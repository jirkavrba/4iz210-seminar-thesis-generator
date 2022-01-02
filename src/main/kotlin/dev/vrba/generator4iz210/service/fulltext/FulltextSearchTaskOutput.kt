package dev.vrba.generator4iz210.service.fulltext

data class DocumentTable(
    val headers: List<String>,
    val rows: List<List<String>>
)

data class FulltextSearchTaskOutput(
    val termFrequency: DocumentTable,
    val normalisedTermFrequency: DocumentTable,
    val dfIdf: DocumentTable,
    val tfIdf: DocumentTable,
    val similarity: DocumentTable
)