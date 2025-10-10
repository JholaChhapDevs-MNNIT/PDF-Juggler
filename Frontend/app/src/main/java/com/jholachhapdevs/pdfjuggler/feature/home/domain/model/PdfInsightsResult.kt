package com.jholachhapdevs.pdfjuggler.feature.home.domain.model

data class PdfInsightsResult(
    val title: String,
    val summary: String,
    val keyPoints: List<String>
)