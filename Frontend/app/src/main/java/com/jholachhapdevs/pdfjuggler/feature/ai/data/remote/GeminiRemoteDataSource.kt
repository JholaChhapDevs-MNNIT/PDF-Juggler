package com.jholachhapdevs.pdfjuggler.feature.ai.data.remote

import com.jholachhapdevs.pdfjuggler.core.networking.httpClient
import com.jholachhapdevs.pdfjuggler.feature.ai.data.model.GeminiContent
import com.jholachhapdevs.pdfjuggler.feature.ai.data.model.GeminiFile
import com.jholachhapdevs.pdfjuggler.feature.ai.data.model.GeminiFileData
import com.jholachhapdevs.pdfjuggler.feature.ai.data.model.GeminiPart
import com.jholachhapdevs.pdfjuggler.feature.ai.data.model.GeminiRequest
import com.jholachhapdevs.pdfjuggler.feature.ai.data.model.GeminiResponse
import com.jholachhapdevs.pdfjuggler.feature.ai.data.model.UploadFileResponse
import com.jholachhapdevs.pdfjuggler.feature.ai.domain.model.ChatMessage
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay

class GeminiRemoteDataSource(
    private val apiKey: String
) {

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta"
    private val uploadBaseUrl = "https://generativelanguage.googleapis.com/upload/v1beta"

    suspend fun sendChat(
        model: String,
        messages: List<ChatMessage>
    ): GeminiResponse {
        val limited = messages.takeLast(20)

        val contents = limited.map { msg ->
            val parts = buildList {
                if (msg.text.isNotBlank()) add(GeminiPart(text = msg.text))
                msg.files.forEach { f ->
                    add(
                        GeminiPart(
                            fileData = com.jholachhapdevs.pdfjuggler.feature.ai.data.model.GeminiFileData(
                                fileUri = f.fileUri,
                                mimeType = f.mimeType
                            )
                        )
                    )
                }
            }
            GeminiContent(role = msg.role, parts = parts)
        }

        val requestBody = GeminiRequest(contents = contents)

        return httpClient.post("$baseUrl/models/$model:generateContent") {
            contentType(ContentType.Application.Json)
            parameter("key", apiKey)
            setBody(requestBody)
        }.body()
    }

    // Generic upload for any mime type (pdf, images, etc.)
    suspend fun uploadFile(
        fileName: String,
        mimeType: String,
        bytes: ByteArray
    ): GeminiFile {
        val initial: UploadFileResponse = httpClient.post("$uploadBaseUrl/files") {
            parameter("key", apiKey)
            headers {
                append("X-Goog-Upload-File-Name", fileName)
                append("X-Goog-Upload-Protocol", "raw")
            }
            contentType(ContentType.parse(mimeType))
            setBody(bytes)
        }.body()

        return waitUntilActive(initial.file)
    }

    // Backward compatibility
    suspend fun uploadImage(
        fileName: String,
        mimeType: String,
        bytes: ByteArray
    ): GeminiFile = uploadFile(fileName, mimeType, bytes)

    private suspend fun waitUntilActive(file: GeminiFile): GeminiFile {
        var current = file
        repeat(40) { // ~20s max
            val refreshed = getFile(current.name)
            if (refreshed.state == "ACTIVE") return refreshed
            delay(500)
            current = refreshed
        }
        return current
    }

    private suspend fun getFile(name: String): GeminiFile {
        return httpClient.get("$baseUrl/$name") {
            parameter("key", apiKey)
        }.body()
    }
}