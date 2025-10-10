package com.jholachhapdevs.pdfjuggler.feature.home.data.model

import kotlinx.serialization.Serializable

@Serializable
data class FlashcardSet(
    val title: String,
    val count: Int,
)