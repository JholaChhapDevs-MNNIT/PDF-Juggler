package com.jholachhapdevs.pdfjuggler

import com.jholachhapdevs.pdfjuggler.feature.flashcards.domain.model.Flashcard
import org.junit.Assert.assertEquals
import org.junit.Test

class FlashcardDummyTest {
    @Test
    fun createsDummyCards() {
        val cards = listOf(
            Flashcard(front = "What is the capital of France?", back = "Paris"),
            Flashcard(front = "Define photosynthesis", back = "Plants convert light energy to chemical energy."),
            Flashcard(front = "H2O is commonly known as?", back = "Water")
        )
        assertEquals(3, cards.size)
        assertEquals("Paris", cards[0].back)
    }
}
