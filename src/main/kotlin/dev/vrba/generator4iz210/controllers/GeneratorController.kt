package dev.vrba.generator4iz210.controllers

import dev.vrba.generator4iz210.service.ThesisGeneratorService
import org.springframework.http.ResponseEntity
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

        @Min(5)
        val query: List<String>,

        @Min(5)
        val frequencies: List<List<Int>>,

        // TODO: Add regex patterns for tasks 2 and 3 + schema definition
    )

    fun generateThesis(@Valid @RequestBody request: GenerateThesisRequest): ResponseEntity<*> {
        val fulltext = service.generateFullText(request.xname, request.inputs, request.query, request.frequencies)

        return ResponseEntity.ok(fulltext)
    }
}