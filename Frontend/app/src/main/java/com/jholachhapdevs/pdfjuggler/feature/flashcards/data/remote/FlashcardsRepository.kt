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

//        val systemPrompt = """
//            Read the attached PDF and create up to $maxCards succinct flashcards.
//            Return ONLY a compact JSON array. Each item must have fields: "front" and "back".
//            Example:
//            [
//              {"front": "What is photosynthesis?", "back": "Process where plants convert light into chemical energy."}
//            ]
//        """.trimIndent()

        val systemPrompt = """
            You are an AI flashcard generator. Your task is to read the attached PDF content and create **up to $maxCards flashcards**.
            
            Requirements:
            1. Each flashcard must be a JSON object with exactly two fields:
               - "front": A concise question, prompt, or keyword phrase.
               - "back": The answer, **always only one or two words**. Do NOT write full sentences.
            2. The questions should be clear, direct, and testable. Avoid vague or long questions.
            3. Return a **JSON array only**, no extra commentary, text, or explanations outside the JSON.
            4. Ensure valid JSON formatting: no trailing commas, correct quotes, no escape errors.
            5. Example output (strict format):
               [
                 {"front": "Capital of France?", "back": "Paris"},
                 {"front": "H2O is known as?", "back": "Water"},
                 {"front": "Atomic number of Hydrogen?", "back": "1"}
               ]
            6. Generate answers that are **precise, factual, and one or two words**.
            7. Avoid including definitions, sentences, or any explanatory text in the "back" field.
            
            Instructions: Only return the JSON array of flashcards. Do not add any text before or after the array.
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