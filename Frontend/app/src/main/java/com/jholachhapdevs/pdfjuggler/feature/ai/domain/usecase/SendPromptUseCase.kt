package com.jholachhapdevs.pdfjuggler.feature.ai.domain.usecase

import com.jholachhapdevs.pdfjuggler.core.util.Resources
import com.jholachhapdevs.pdfjuggler.feature.ai.data.model.GeminiResponse
import com.jholachhapdevs.pdfjuggler.feature.ai.data.remote.GeminiRemoteDataSource
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.model.ChatMessage

/**
 * Sends the full chat history (plus a persona preface) to Gemini and returns the next model message.
 */
class SendPromptUseCase(
    private val remote: GeminiRemoteDataSource,
    private val modelName: String = Resources.DEFAULT_AI_MODEL,
    private val assistantName: String = "Ringmaster"
) {
    suspend operator fun invoke(messages: List<ChatMessage>): ChatMessage {
        // Send only the user's provided messages; no persona preface.
        val limited = messages.takeLast(20)
        val response: GeminiResponse = remote.sendChat(modelName, limited)

        // Collect any text across all candidates/parts; join if multiple chunks
        val aggregated = response.candidates
            ?.flatMap { it.content?.parts.orEmpty() }
            ?.mapNotNull { it.text }
            ?.filter { it.isNotBlank() }
            ?.joinToString(separator = "\n\n")

        val text = aggregated ?: "I couldn't get a response from the model. Please try again."
        return ChatMessage(role = "model", text = text)
    }
}