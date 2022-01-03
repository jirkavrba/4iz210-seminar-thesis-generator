package dev.vrba.generator4iz210.domain

data class TypedXmlNode(
    val name: String,
    val type: String,
    val value: String,
    val children: List<TypedXmlNode>
)