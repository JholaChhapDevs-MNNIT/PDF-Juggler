package com.jholachhapdevs.pdfjuggler

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PdfJugglerBackendApplication

fun main(args: Array<String>) {
	runApplication<PdfJugglerBackendApplication>(*args)
}
