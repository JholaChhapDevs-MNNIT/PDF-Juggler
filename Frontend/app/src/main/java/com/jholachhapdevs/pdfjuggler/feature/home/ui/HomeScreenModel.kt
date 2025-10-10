package com.jholachhapdevs.pdfjuggler.feature.home.ui

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.jholachhapdevs.pdfjuggler.core.util.FileType
import com.jholachhapdevs.pdfjuggler.core.util.openFilePicker
import com.jholachhapdevs.pdfjuggler.feature.home.domain.usecase.ExtractKeyTextUseCase
import com.jholachhapdevs.pdfjuggler.feature.flashcards.domain.model.Flashcard
import com.jholachhapdevs.pdfjuggler.feature.flashcards.domain.usecase.GenerateFlashcardsFromPdfUseCase
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.jholachhapdevs.pdfjuggler.feature.home.data.model.FlashcardSet
import com.jholachhapdevs.pdfjuggler.feature.home.domain.usecase.GetSetTitleUseCase

class HomeScreenModel(
    private val extractKeyText: ExtractKeyTextUseCase = ExtractKeyTextUseCase(),
    private val generateFlashcards: GenerateFlashcardsFromPdfUseCase = GenerateFlashcardsFromPdfUseCase(),
    private val getSetTitleUseCase: GetSetTitleUseCase = GetSetTitleUseCase()
) : ScreenModel {

    var selectedPdfUri: String? by mutableStateOf(null)
        private set
    var selectedPdfName: String? by mutableStateOf(null)
        private set

    var isExtracting by mutableStateOf(false)
        private set
    var resultText by mutableStateOf("")
        private set
    var error by mutableStateOf<String?>(null)
        private set

    // Flashcards generation state
    var isGeneratingCards by mutableStateOf(false)
        private set
    var cards: List<Flashcard> by mutableStateOf(emptyList())
        private set
    var cardsError by mutableStateOf<String?>(null)
        private set

    fun pickPdf(context: Context) {
        screenModelScope.launch {
            resultText = ""
            error = null
            val uri = openFilePicker(
                context = context,
                type = FileType.DOCUMENT
            )
            selectedPdfUri = uri
            selectedPdfName = uri?.let { getDisplayName(context, it.toUri()) }
        }
    }

    fun extractKeyText(context: Context) {
        val uri = selectedPdfUri ?: return
        if (isExtracting) return
        screenModelScope.launch {
            isExtracting = true
            error = null
            try {
                val res = extractKeyText(
                    context = context,
                    contentUri = uri.toUri(),
                    displayName = selectedPdfName ?: "document.pdf"
                )
                resultText = res.text
            } catch (t: Throwable) {
                error = t.message ?: "Extraction failed"
            } finally {
                isExtracting = false
            }
        }
    }

    fun generateFlashcards(context: Context, maxCards: Int = 20) {
        val uri = selectedPdfUri ?: return
        if (isGeneratingCards) return
        screenModelScope.launch {
            isGeneratingCards = true
            cardsError = null
            try {
                val result = generateFlashcards(
                    context = context,
                    contentUri = uri.toUri(),
                    displayName = selectedPdfName ?: "document.pdf",
                    maxCards = maxCards
                )
                cards = result
            } catch (t: Throwable) {
                cardsError = t.message ?: "Failed to generate flashcards"
            } finally {
                isGeneratingCards = false
            }
        }
    }

    private fun getDisplayName(context: Context, uri: Uri): String? {
        var name: String? = null
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri, null, null, null, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index >= 0) name = cursor.getString(index)
            }
        } catch (_: Throwable) {
        } finally {
            cursor?.close()
        }
        return name
    }

    fun getPdfInsights(context: Context, onResult: (FlashcardSet?) -> Unit, onError: (String) -> Unit) {
        val uri = selectedPdfUri ?: return
        screenModelScope.launch {
            try {
                val insights = getSetTitleUseCase(
                    context = context,
                    contentUri = uri.toUri(),
                    displayName = selectedPdfName ?: "document.pdf"
                )
                onResult(insights)
            } catch (t: Throwable) {
                onError(t.message ?: "Failed to get PDF insights")
            }
        }
    }
}
