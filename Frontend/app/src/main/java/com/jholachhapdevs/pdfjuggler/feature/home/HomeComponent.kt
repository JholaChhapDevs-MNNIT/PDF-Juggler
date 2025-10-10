package com.jholachhapdevs.pdfjuggler.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jholachhapdevs.pdfjuggler.core.components.KAppBar
import com.mikepenz.markdown.m3.Markdown

@Composable
fun HomeComponent(
    screenModel: HomeScreenModel
) {
    val context = LocalContext.current

    Scaffold(
        topBar = { KAppBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Import PDF and extract key text",
                style = MaterialTheme.typography.titleLarge
            )

            // File actions
            Surface(tonalElevation = 0.dp) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { screenModel.pickPdf(context) }) {
                        Text("Choose PDF")
                    }
                    val name = screenModel.selectedPdfName
                    Text(
                        text = name?.let { "Selected: $it" } ?: "No file selected",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    OutlinedButton(
                        onClick = { screenModel.extractKeyText(context) },
                        enabled = screenModel.selectedPdfUri != null && !screenModel.isExtracting
                    ) { Text("Extract Key Text") }
                }
            }

            HorizontalDivider()

            // Result area
            Surface(tonalElevation = 0.dp, modifier = Modifier.fillMaxSize()) {
                if (screenModel.isExtracting) {
                    Column(
                        Modifier.fillMaxWidth().fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(8.dp))
                        Text("Extracting...")
                    }
                } else if (screenModel.error != null) {
                    Text(
                        text = screenModel.error!!,
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (screenModel.resultText.isNotBlank()) {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        SelectionContainer {
                            Markdown(
                                content = screenModel.resultText,
                                modifier = Modifier
                            )
                        }
                    }
                } else {
                    Text("Result will appear here.")
                }
            }
        }
    }
}
