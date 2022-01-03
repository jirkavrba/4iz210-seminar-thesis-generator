package dev.vrba.generator4iz210.service.feed

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import dev.vrba.generator4iz210.domain.RegexXmlPattern
import dev.vrba.generator4iz210.domain.TypedXmlNode
import dev.vrba.generator4iz210.service.XmlFormatter
import org.springframework.stereotype.Component

@Component
class ProductFeedTaskGenerator(private val formatter: XmlFormatter) {

    fun generateTaskOutput(xml: String, patterns: List<RegexXmlPattern>): ProductFeedTaskOutput {
        val schema = generateSchema(patterns)

        val tree = XmlMapper().readTree(xml)
        val json = convertToJson(tree)
        val csv = convertToCsv(tree, patterns)

        return ProductFeedTaskOutput(schema, json, csv)
    }

    private fun generateSchema(patterns: List<RegexXmlPattern>): String {
        val nodes = patterns.flatMap { it.xml.children }
        val references = nodes.joinToString("\n") { "<xs:element name=\"${it.name}\" type=\"${it.name}_TYPE\"/>" }
        val types = nodes.joinToString("\n") { createTypeSchema(it, true) }

        return """
         <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema" elementFormDefault="qualified">
             <xs:element name="SHOP">
                 <xs:complexType>
                     <xs:sequence maxOccurs="unbounded">
                         <xs:element name="SHOPITEM" type="SHOPITEM_TYPE"/>
                     </xs:sequence>
                 </xs:complexType>
             </xs:element>
             <xs:complexType name="SHOPITEM_TYPE">
                <xs:all>
                    $references
                </xs:all>
             </xs:complexType>
             $types
         </xs:schema>
        """.trimIndent()
           .let { formatter.prettyPrint(it) }
    }

    private fun createTypeSchema(node: TypedXmlNode, root: Boolean = false): String =
        when (node.type) {
            "string" -> if (!root) "<xs:element name=\"${node.name}\" type=\"xs:string\"/>"
                        else  """
                              <xs:simpleType name="${node.name}_TYPE">
                                <xs:restriction base="xs:string">
                                    <!-- Zde je potřeba doplnit vlastní restrikce, nebo smazat -->
                                </xs:restriction>
                              </xs:simpleType>
                              """.trimIndent()

            "integer" -> if (!root) "<xs:element name=\"${node.name}\" type=\"xs:integer\"/>"
                         else "<xs:simpleType name=\"${node.name}_TYPE\" type=\"xs:integer\"/>"

            else -> """
                <xs:complexType name="${node.name}_TYPE">
                    <xs:all>
                        ${node.children.joinToString("\n") { createTypeSchema(it) }}
                    </xs:all>
                </xs:complexType> 
            """.trimIndent()
        }

    private fun convertToJson(tree: JsonNode): String = JsonMapper()
        .writeValueAsString(tree)
        .replaceFirst("{", "{\"SHOP\": {")
        .replaceAfterLast("]", "}}")

    private fun convertToCsv(tree: JsonNode, patterns: List<RegexXmlPattern>): String {
        val nodes = patterns.flatMap { it.xml.children }
        val headers = nodes.flatMap { headerName(it) }
        val data = tree.elements().next()
            .elements()
            .asSequence()
            .mapIndexed { index, node ->
                listOf("input${index + 1}.txt") +
                    headers.map { header ->
                        header.split("-")
                            .fold(node) { current, key -> current.get(key) }
                            .asText()
                    }
            }
            .map { it.joinToString(",") }
            .joinToString("\n")


        val header = (listOf("input") + headers).joinToString(",") { it.lowercase() }

        return header + "\n" + data
    }

    private fun headerName(node: TypedXmlNode): List<String> {
        if (node.type == "complex") {
            return node.children.flatMap { child ->
                headerName(child).map {
                    "${node.name}-" + it
                }
            }
        }

        return listOf(node.name)
    }
}