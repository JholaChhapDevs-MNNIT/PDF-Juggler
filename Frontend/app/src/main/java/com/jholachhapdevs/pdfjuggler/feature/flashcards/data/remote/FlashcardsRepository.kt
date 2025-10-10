package com.jholachhapdevs.pdfjuggler.feature.flashcards.data.remote

import android.content.Context
import android.net.Uri
import com.jholachhapdevs.pdfjuggler.feature.ai.data.remote.GeminiRemoteDataSource
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.usecase.SendPromptUseCase
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.usecase.UploadFileUseCase
import com.jholachhapdevs.pdfjuggler.feature.flashcards.data.model.FlashcardDto
import com.kanhaji.basics.util.Env
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class FlashcardsRepository(
    private val remote: GeminiRemoteDataSource = GeminiRemoteDataSource(apiKey = Env.geminiApiKey),
    private val upload: UploadFileUseCase = UploadFileUseCase(remote),
    private val send: SendPromptUseCase = SendPromptUseCase(remote),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    suspend fun generateFromPdf(
        context: Context,
        uri: Uri,
        displayName: String,
        maxCards: Int = 20
    ): List<FlashcardDto> {
        val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: ByteArray(0)
        }
        val fileUri = upload(
            fileName = displayName,
            mimeType = "application/pdf",
            bytes = bytes
        )

        val systemPrompt = """
            Read the attached PDF and create up to $maxCards succinct flashcards.
            Return ONLY a compact JSON array. Each item must have fields: "front" and "back".
            Example:
            [
              {"front": "What is photosynthesis?", "back": "Process where plants convert light into chemical energy."}
            ]
        """.trimIndent()

        val modelMessage = send(
            listOf(
                com.jholachhapdevs.pdfjuggler.feature.ai.domain.model.ChatMessage(
                    role = "user",
                    text = systemPrompt,
                    files = listOf(
                        com.jholachhapdevs.pdfjuggler.feature.ai.domain.model.AttachedFile(
                            mimeType = "application/pdf",
                            fileUri = fileUri
                        )
                    )
                )
            )
        )

        val text = modelMessage.text.trim()
        // Try direct parse; if fails, try to extract JSON block
        return runCatching { json.decodeFromString<List<FlashcardDto>>(text) }
            .getOrElse {
                val extracted = extractJsonArray(text)
                json.decodeFromString(extracted)
            }
    }

    private fun extractJsonArray(source: String): String {
        val start = source.indexOf('[')
        val end = source.lastIndexOf(']')
        if (start >= 0 && end > start) return source.substring(start, end + 1)
        // Try code fence style
        val fenceStart = source.indexOf("```")
        val fenceEnd = source.lastIndexOf("```")
        if (fenceStart >= 0 && fenceEnd > fenceStart) {
            val inner = source.substring(fenceStart + 3, fenceEnd)
            val s = inner.indexOf('[')
            val e = inner.lastIndexOf(']')
            if (s >= 0 && e > s) return inner.substring(s, e + 1)
        }
        return "[]"
    }
}