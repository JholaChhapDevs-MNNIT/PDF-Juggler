package com.jholachhapdevs.pdfjuggler.feature.home.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class FlashcardSet(
    val title: String,
    val count: Int,
)