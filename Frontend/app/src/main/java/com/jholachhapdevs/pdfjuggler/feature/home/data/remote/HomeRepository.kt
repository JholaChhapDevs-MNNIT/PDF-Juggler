package com.jholachhapdevs.pdfjuggler.feature.home.data.remote

import android.content.Context
import android.net.Uri
import com.jholachhapdevs.pdfjuggler.feature.ai.data.remote.GeminiRemoteDataSource
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.model.AttachedFile
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.model.ChatMessage
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.usecase.SendPromptUseCase
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.usecase.UploadFileUseCase
import com.jholachhapdevs.pdfjuggler.feature.home.data.model.FlashcardSet
import com.jholachhapdevs.pdfjuggler.feature.home.data.model.KeyTextResult
import com.jholachhapdevs.pdfjuggler.feature.home.domain.model.PdfInsightsResult
import com.kanhaji.basics.util.Env
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepository(
    private val remote: GeminiRemoteDataSource = GeminiRemoteDataSource(apiKey = Env.geminiApiKey),
    private val upload: UploadFileUseCase = UploadFileUseCase(remote),
    private val send: SendPromptUseCase = SendPromptUseCase(remote)
) {
    suspend fun extractKeyText(
        context: Context,
        contentUri: Uri,
        displayName: String
    ): KeyTextResult {
        val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(contentUri)?.use { it.readBytes() }
                ?: ByteArray(0)
        }
        val fileUri = upload(
            fileName = displayName,
            mimeType = "application/pdf",
            bytes = bytes
        )
        val prompt = """
            Extract the key points from the attached PDF.
            Focus on: headings, definitions, formulas, steps, and actionable items.
            Return:
            - A concise bulleted list of key points
            - A short 3-4 sentence summary
        """.trimIndent()
        val reply = send(
            listOf(
                ChatMessage(
                    role = "user",
                    text = prompt,
                    files = listOf(AttachedFile(mimeType = "application/pdf", fileUri = fileUri))
                )
            )
        )
        return KeyTextResult(text = reply.text)
    }

    suspend fun getSetTitle(
        context: Context,
        contentUri: Uri,
        displayName: String
    ): FlashcardSet {
        val bytes = withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(contentUri)?.use { it.readBytes() }
                ?: throw IllegalStateException("Unable to read PDF bytes")
        }

        val fileUri = upload(
            fileName = displayName,
            mimeType = "application/pdf",
            bytes = bytes
        )

        // Ask only for a short, plain title.
        val prompt = """
        Read the attached PDF and return ONLY its main subject/title.
        Constraints:
        - 1 or 2 words, Title Case.
        - No quotes, no punctuation at the end.
        - No explanations, no markdown, no code fences.
        If unsure, return your best guess based on headings and early pages.
    """.trimIndent()

        val reply = send(
            listOf(
                ChatMessage(
                    role = "user",
                    text = prompt,
                    files = listOf(
                        AttachedFile(
                            fileUri = fileUri,
                            mimeType = "application/pdf"
                        )
                    )
                )
            )
        )

        // Normalize to a single clean line without fences/quotes/punctuation.
        val raw = reply.text.orEmpty().trim()
        val unwrapped = unwrapCodeFences(raw)
        val firstLine = unwrapped.lineSequence().firstOrNull().orEmpty().trim()
        val cleaned = firstLine
            .trim().trim('"', '\'', '`')
            .removeSuffix(".").removeSuffix("!").removeSuffix("?")
            .ifBlank { displayName.substringBeforeLast('.', displayName) }

//        return PdfInsightsResult(
//            title = cleaned.take(100),
//            summary = "",
//            keyPoints = emptyList()
//        )
        return FlashcardSet(
            title = cleaned.take(100),
            count = -1,
        )
    }

    private fun unwrapCodeFences(text: String): String {
        val t = text.trim()
        val fenceStart = t.indexOf("```")
        if (fenceStart >= 0) {
            val after =
                t.indexOf('\n', fenceStart + 3).let { if (it >= 0) it + 1 else fenceStart + 3 }
            val fenceEnd = t.lastIndexOf("```")
            if (fenceEnd > after) return t.substring(after, fenceEnd).trim()
        }
        return t
    }

    private fun extractJson(text: String): String {
        val trimmed = text.trim()
        // If wrapped in ```json ... ``` or ``` ... ```
        val fenceStart = trimmed.indexOf("```")
        if (fenceStart >= 0) {
            val after = trimmed.indexOf('\n', fenceStart + 3)
                .let { if (it >= 0) it + 1 else fenceStart + 3 }
            val fenceEnd = trimmed.lastIndexOf("```")
            if (fenceEnd > after) {
                return trimmed.substring(after, fenceEnd).trim()
            }
        }
        // Fallback: try to find the first '{' to last '}' span
        val first = trimmed.indexOf('{')
        val last = trimmed.lastIndexOf('}')
        if (first >= 0 && last > first) {
            return trimmed.substring(first, last + 1)
        }
        return trimmed // best effort
    }
}
