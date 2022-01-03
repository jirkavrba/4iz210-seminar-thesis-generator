package dev.vrba.generator4iz210.domain

import javax.validation.constraints.NotBlank

data class RegexXmlPattern(
    @NotBlank
    val regex: String,

    @NotBlank
    val description: String,

    val xml: TypedXmlNode,
)