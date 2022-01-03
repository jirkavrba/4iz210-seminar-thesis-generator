package dev.vrba.generator4iz210.service.feed

data class ProductFeedTaskOutput(
    val schema: String,
    val json: String,
    val csv: String,
)