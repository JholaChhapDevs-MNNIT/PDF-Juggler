package com.jholachhapdevs.pdfjuggler.feature.flashcards.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import com.jholachhapdevs.pdfjuggler.feature.flashcards.domain.model.Flashcard

class FlashCardScreenModel(
    initialCards: List<Flashcard>
): ScreenModel {
    
    var cards: List<Flashcard> by mutableStateOf(initialCards)
        private set
        
    fun updateCards(newCards: List<Flashcard>) {
        cards = newCards
    }
}
