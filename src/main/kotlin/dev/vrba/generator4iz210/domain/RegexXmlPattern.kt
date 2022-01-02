package dev.vrba.generator4iz210.domain

import javax.validation.constraints.NotBlank

data class RegexXmlPattern(
    @NotBlank
    val regex: String,

    @NotBlank
    val description: String,

    // TODO: Add support structure for generating XML nodes and XSD schema
)