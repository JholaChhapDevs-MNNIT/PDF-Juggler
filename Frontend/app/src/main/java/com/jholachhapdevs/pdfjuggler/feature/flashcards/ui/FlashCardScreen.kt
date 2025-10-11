package com.jholachhapdevs.pdfjuggler.feature.flashcards.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.jholachhapdevs.pdfjuggler.feature.flashcards.domain.model.Flashcard

data class FlashCardScreen(
    val flashCards: List<Flashcard>
): Screen {
    @Composable
    override fun Content() {

        val flashCardScreenModel = rememberScreenModel {
            FlashCardScreenModel(flashCards)
        }

        FlashCardComponent(flashCardScreenModel)

    }
}
