package dev.vrba.generator4iz210.domain

data class TypedXmlNode(
    val name: String,
    val type: String,
    val value: String,
    val children: List<TypedXmlNode>
) {
    override fun toString(): String {
        val content = if (type != "complex") value
                      else children.joinToString("\n") { it.toString() }

        return "<$name>$content</$name>"
    }
}