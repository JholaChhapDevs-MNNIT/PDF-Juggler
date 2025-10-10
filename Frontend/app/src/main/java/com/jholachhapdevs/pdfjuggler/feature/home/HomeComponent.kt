package com.jholachhapdevs.pdfjuggler.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kanhaji.basics.composables.KButton
import com.jholachhapdevs.pdfjuggler.core.components.KAppBar

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
                    KButton(onClick = { screenModel.pickPdf(context) }) {
                        Text("Choose PDF")
                    }
                    val name = screenModel.selectedPdfName
                    Text(
                        text = name?.let { "Selected: $it" } ?: "No file selected",
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KButton(
                            onClick = { screenModel.extractKeyText(context) },
                            enabled = screenModel.selectedPdfUri != null && !screenModel.isExtracting
                        ) { Text("Extract Key Text") }
                        KButton(
                            onClick = { screenModel.generateFlashcards(context) },
                            enabled = screenModel.selectedPdfUri != null && !screenModel.isGeneratingCards
                        ) { Text("Generate Flashcards") }
                    }
                }
            }

            HorizontalDivider()

            // Result area
            Surface(tonalElevation = 0.dp, modifier = Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize()) {
                    // Key text result panel
                    if (screenModel.isExtracting) {
                        Column(
                            Modifier.fillMaxWidth().fillMaxHeight(0.3f),
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
                        Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                            Text(
                                text = screenModel.resultText,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Text("Result will appear here.")
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    // Flashcards panel
                    if (screenModel.isGeneratingCards) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(8.dp))
                            Text("Generating flashcards...")
                        }
                    } else if (screenModel.cardsError != null) {
                        Text(
                            text = screenModel.cardsError!!,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else if (screenModel.cards.isNotEmpty()) {
                        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Flashcards (${screenModel.cards.size})", style = MaterialTheme.typography.titleMedium)
                            screenModel.cards.forEach { card ->
                                Surface(tonalElevation = 0.dp) {
                                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(text = "Q: ${card.front}", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "A: ${card.back}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
