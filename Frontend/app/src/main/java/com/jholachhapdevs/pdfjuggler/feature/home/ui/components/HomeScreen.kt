package com.jholachhapdevs.pdfjuggler.feature.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jholachhapdevs.pdfjuggler.feature.home.domain.model.FlashcardSet
import com.jholachhapdevs.pdfjuggler.feature.home.ui.HomeScreenModel

@Composable
fun HomeScreen(
    screenModel: HomeScreenModel
) {
    val context = LocalContext.current

    Scaffold(
        topBar = { HomeTopBar(onImportClick = { screenModel.pickPdf(context) }) },
        bottomBar = { HomeBottomBar() },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SearchBar() }
//            item { FilterRow() }

            item {
                ImportStatusCard(
                    name = screenModel.selectedPdfName,
                    onClick = { screenModel.pickPdf(context) }
                )
            }
            item { ErrorBanner(text = screenModel.error ?: screenModel.cardsError) }

            if (screenModel.selectedPdfUri != null) {
                item {
                    ActionRow(
                        enabled = screenModel.selectedPdfUri != null,
                        isExtracting = screenModel.isExtracting,
                        isGenerating = screenModel.isGeneratingCards,
                        onExtract = { screenModel.extractKeyText(context) },
                        onGenerate = { screenModel.generateFlashcards(context) }
                    )
                }
            }

            if (screenModel.isExtracting || screenModel.isGeneratingCards) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }

            if (screenModel.resultText.isNotEmpty()) {
                item {
                    ResultPreviewWithMarkdown(
                        title = "Extracted summary",
                        body = screenModel.resultText
                    )
                }
            }

            if (screenModel.cards.isNotEmpty()) {
                item {
                    FlashcardItem(
                        FlashcardSet(
                            title = screenModel.getSetTitle(
                                context = context,
                                onResult = {

                                },
                                onError = {

                                }
                            )
                        )
                    )
                }
//                item {
//                    Text(
//                        text = "Generated Cards (${screenModel.cards.size})",
//                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
//                        color = MaterialTheme.colorScheme.onBackground,
//                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
//                    )
//                }
//                items(
//                    items = screenModel.cards,
//                    key = { it.front + "|" + it.back }
//                ) { card ->
//                    Row(Modifier.padding(horizontal = 16.dp)) {
//                        Text(
//                            text = "Q: ${card.front}\nA: ${card.back}",
//                            style = MaterialTheme.typography.bodyMedium,
//                            color = MaterialTheme.colorScheme.onSurface,
//                            maxLines = 6,
//                            overflow = TextOverflow.Ellipsis
//                        )
//                    }
//                }
            }
        }
    }
}
