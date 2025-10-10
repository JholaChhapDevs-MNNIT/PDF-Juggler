package com.jholachhapdevs.pdfjuggler.feature.home.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PdfInsights(
    val title: String,
    val summary: String,
    val keyPoints: List<String>
)