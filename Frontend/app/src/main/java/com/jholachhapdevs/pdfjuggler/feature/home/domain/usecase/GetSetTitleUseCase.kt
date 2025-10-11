package com.jholachhapdevs.pdfjuggler.feature.home.domain.usecase

import android.content.Context
import android.net.Uri
import com.jholachhapdevs.pdfjuggler.feature.home.data.remote.HomeRepository
import com.jholachhapdevs.pdfjuggler.feature.home.data.model.PdfInsights

class GetSetTitleUseCase(
    private val repo: HomeRepository = HomeRepository()
) {
    suspend operator fun invoke(
        context: Context,
        contentUri: Uri,
        displayName: String
    ): PdfInsights {
        val res = repo.getSetTitle(context, contentUri, displayName)
        return PdfInsights(
            title = res.title,
            summary = "",
            keyPoints = emptyList()
        )
    }
}