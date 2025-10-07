package com.jholachhapdevs.pdfjuggler.feature.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.jholachhapdevs.pdfjuggler.core.util.FileType
import com.jholachhapdevs.pdfjuggler.core.util.openFilePicker
import kotlinx.coroutines.launch

class HomeScreenModel : ScreenModel {

    var selectedPdfUri: String? by mutableStateOf(null)
        private set

    fun pickPdf(context: Context) {
        screenModelScope.launch {
            selectedPdfUri = openFilePicker(
                context = context,
                type = FileType.DOCUMENT
            )
        }
    }
}