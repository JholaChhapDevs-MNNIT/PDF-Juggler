package com.jholachhapdevs.pdfjuggler.controller

import com.jholachhapdevs.pdfjuggler.entity.FlashCard
import com.jholachhapdevs.pdfjuggler.service.FlashCardService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/flashcard")
class FlashCardController(
    private val flashCardService: FlashCardService
) {

    @PostMapping("/create")
    fun createFlashCard(@Valid @RequestBody flashCard: FlashCard): ResponseEntity<FlashCard> {
        val saved = flashCardService.createFlashCard(flashCard)
        return ResponseEntity.ok(saved)
    }

    @GetMapping("/getAllFlashCards")
    fun getAllFlashCards(): ResponseEntity<List<FlashCard>> {
        val flashcards = flashCardService.getAllFlashCards()
        return ResponseEntity.ok(flashcards)
    }

    @GetMapping("/{id}")
    fun getFlashCardById(@PathVariable id: String): ResponseEntity<FlashCard> {
        val flashcard = flashCardService.getFlashCardById(id)
        return ResponseEntity.ok(flashcard)
    }

    @PutMapping("/{id}")
    fun updateFlashCard(
        @PathVariable id: String,
        @Valid @RequestBody flashCard: FlashCard
    ): ResponseEntity<FlashCard> {
        val updated = flashCardService.updateFlashCard(id, flashCard)
        return ResponseEntity.ok(updated)
    }

    @DeleteMapping("/{id}")
    fun deleteFlashCard(@PathVariable id: String): ResponseEntity<Void> {
        flashCardService.deleteFlashCard(id)
        return ResponseEntity.noContent().build()
    }
}
