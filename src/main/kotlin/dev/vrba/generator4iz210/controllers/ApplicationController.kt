package dev.vrba.generator4iz210.controllers

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class ApplicationController {

    @GetMapping
    fun index(): String = "index"

}