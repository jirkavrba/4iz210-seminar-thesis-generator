package dev.vrba.generator4iz210.controllers

import dev.vrba.generator4iz210.domain.RegexXmlPattern
import dev.vrba.generator4iz210.service.ThesisGeneratorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.*

@RestController
@RequestMapping("/generator")
class GeneratorController(private val service: ThesisGeneratorService) {

    data class GenerateThesisRequest(
        @NotBlank
        @Pattern(regexp = "^[a-z0-9]{6}$")
        val xname: String,

        @Min(3)
        @Max(5)
        val inputs: List<String>,

        // Map of "base query" -> ["variations"]
        @Min(5)
        val query: Map<String, List<String>>,

        @Min(5)
        val patterns: List<RegexXmlPattern>
    )

    @PostMapping
    fun generateThesis(@Valid @RequestBody request: GenerateThesisRequest): ResponseEntity<*> {
        val fulltext = service.generateFullText(request.inputs, request.query)
        val extraction = service.generateExtraction(request.inputs, request.patterns)
        val feed = service.generateProductFeed(extraction.products, request.patterns)

        return ResponseEntity.ok(
            mapOf(
                "fulltext" to fulltext,
                "extraction" to extraction,
                "feed" to feed,
            )
        )
    }
}