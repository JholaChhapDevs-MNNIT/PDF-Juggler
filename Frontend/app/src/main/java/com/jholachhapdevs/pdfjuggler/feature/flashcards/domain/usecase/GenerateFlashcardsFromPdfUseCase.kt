package com.jholachhapdevs.pdfjuggler.feature.flashcards.domain.usecase

import android.content.Context
import android.net.Uri
import com.jholachhapdevs.pdfjuggler.feature.flashcards.data.remote.FlashcardsRepository
import com.jholachhapdevs.pdfjuggler.feature.flashcards.domain.model.Flashcard

class GenerateFlashcardsFromPdfUseCase(
    private val repo: FlashcardsRepository = FlashcardsRepository()
) {
    suspend operator fun invoke(
        context: Context,
        contentUri: Uri,
        displayName: String,
        maxCards: Int = 20
    ): List<Flashcard> {
        val dtos = repo.generateFromPdf(context, contentUri, displayName, maxCards)
        return dtos.map { Flashcard(front = it.front, back = it.back) }
    }
}