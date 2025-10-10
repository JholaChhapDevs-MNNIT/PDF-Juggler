package com.jholachhapdevs.pdfjuggler.feature.home.data.remote

import android.content.Context
import android.net.Uri
import com.jholachhapdevs.pdfjuggler.feature.home.data.model.KeyTextResult
import com.jholachhapdevs.pdfjuggler.feature.ai.data.remote.GeminiRemoteDataSource
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.model.AttachedFile
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.model.ChatMessage
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.usecase.SendPromptUseCase
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.usecase.UploadFileUseCase
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
}
