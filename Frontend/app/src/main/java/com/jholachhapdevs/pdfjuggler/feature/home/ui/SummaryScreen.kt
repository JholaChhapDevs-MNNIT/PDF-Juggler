package com.jholachhapdevs.pdfjuggler.feature.home.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.jholachhapdevs.pdfjuggler.core.components.KAppBar
import com.mikepenz.markdown.m3.Markdown

data class SummaryScreen(
    val summary: String
) : Screen {

    @Composable
    override fun Content() {
        Scaffold(
            topBar = {
                KAppBar(
                    title = "Summary",
                    showSettingsIcon = false
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (summary.isNotBlank()) {
                    Markdown(content = summary)
                } else {
                    Text(
                        text = "No summary available.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}