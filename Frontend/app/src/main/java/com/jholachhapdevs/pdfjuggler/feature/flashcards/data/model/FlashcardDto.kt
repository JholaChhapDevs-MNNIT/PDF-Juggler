package com.jholachhapdevs.pdfjuggler.feature.flashcards.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlashcardDto(
    @SerialName("front") val front: String,
    @SerialName("back") val back: String
)