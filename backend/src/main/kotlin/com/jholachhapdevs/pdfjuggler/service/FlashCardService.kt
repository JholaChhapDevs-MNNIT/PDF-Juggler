package com.jholachhapdevs.pdfjuggler.service

import com.jholachhapdevs.pdfjuggler.entity.FlashCard
import com.jholachhapdevs.pdfjuggler.repository.FlashCardRepository
import jakarta.validation.Valid
import org.springframework.stereotype.Service

@Service
class FlashCardService(
    private val flashCardRepository: FlashCardRepository
) {

    fun createFlashCard(@Valid flashCard: FlashCard): FlashCard {
        return flashCardRepository.save(flashCard)
    }

    fun getAllFlashCards(): List<FlashCard> {
        return flashCardRepository.findAll()
    }

    fun getFlashCardById(id: String): FlashCard {
        return flashCardRepository.findById(id)
            .orElseThrow { IllegalArgumentException("FlashCard not found with id: $id") }
    }

    fun updateFlashCard(id: String, @Valid updatedFlashCard: FlashCard): FlashCard {
        val existing = getFlashCardById(id)
        val flashCardToSave = existing.copy(
            question = updatedFlashCard.question,
            answer = updatedFlashCard.answer
        )
        return flashCardRepository.save(flashCardToSave)
    }

    fun deleteFlashCard(id: String) {
        if (!flashCardRepository.existsById(id)) {
            throw IllegalArgumentException("FlashCard not found with id: $id")
        }
        flashCardRepository.deleteById(id)
    }
}
