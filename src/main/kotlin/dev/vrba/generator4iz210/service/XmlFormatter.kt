package dev.vrba.generator4iz210.service

import org.springframework.stereotype.Service
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

@Service
class XmlFormatter {

    fun prettyPrint(xml: String): String {
        val transformer = TransformerFactory.newInstance()
            .newTransformer()
            .apply {
                setOutputProperty(OutputKeys.INDENT, "yes")
                setOutputProperty("{https://xml.apache.org/xslt}indent-amount", "4")
            }

        val source = StreamSource(StringReader(xml))
        val result = StreamResult(StringWriter())

        transformer.transform(source, result)

        return result.writer.toString()
    }
}