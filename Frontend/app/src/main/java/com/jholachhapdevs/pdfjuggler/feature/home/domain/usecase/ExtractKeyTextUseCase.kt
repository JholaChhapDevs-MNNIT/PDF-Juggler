package com.jholachhapdevs.pdfjuggler.feature.home.domain.usecase

import android.content.Context
import android.net.Uri
import com.jholachhapdevs.pdfjuggler.feature.home.data.remote.HomeRepository
import com.jholachhapdevs.pdfjuggler.feature.home.domain.model.KeyText

class ExtractKeyTextUseCase(
    private val repo: HomeRepository = HomeRepository()
) {
    suspend operator fun invoke(
        context: Context,
        contentUri: Uri,
        displayName: String
    ): KeyText {
        val res = repo.extractKeyText(context, contentUri, displayName)
        return KeyText(text = res.text)
    }
}
