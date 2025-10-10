
import com.jholachhapdevs.pdfjuggler.core.util.Resources
import com.jholachhapdevs.pdfjuggler.feature.ai.data.remote.GeminiRemoteDataSource
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.model.ChatMessage

/**
 * Minimal wrapper for a single text prompt -> text response.
 * Does not add any persona or attach files; it simply forwards the message
 * to the configured model and returns the first text candidate (or empty string).
 */
class AskSimpleUseCase(
    private val remote: GeminiRemoteDataSource,
    private val modelName: String = Resources.DEFAULT_AI_MODEL
) {
    suspend operator fun invoke(prompt: String): String {
        val response = remote.sendChat(
            model = modelName,
            messages = listOf(ChatMessage(role = "user", text = prompt))
        )
        return response.candidates
            ?.firstOrNull()
            ?.content
            ?.parts
            ?.firstOrNull()
            ?.text
            ?: ""
    }
}