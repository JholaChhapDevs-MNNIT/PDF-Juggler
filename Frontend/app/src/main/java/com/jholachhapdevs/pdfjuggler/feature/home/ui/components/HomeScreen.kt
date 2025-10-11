package com.jholachhapdevs.pdfjuggler.feature.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jholachhapdevs.pdfjuggler.core.util.KToast
import com.jholachhapdevs.pdfjuggler.feature.flashcards.ui.FlashCardScreen
import com.jholachhapdevs.pdfjuggler.feature.home.data.model.FlashcardSet
import com.jholachhapdevs.pdfjuggler.feature.home.ui.HomeScreenModel
import com.jholachhapdevs.pdfjuggler.feature.home.ui.SummaryScreen

@Composable
fun HomeScreen(
    screenModel: HomeScreenModel
) {
    val context = LocalContext.current
    var headingTitle: String? = null
    val navigator = LocalNavigator.currentOrThrow
    // Fetch insights title when a PDF is selected
    screenModel.getPdfInsights(
        context = context,
        onResult = { headingTitle = it.title },
        onError = { /* ignore for now */ }
    )

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
                        onExtract = {
                            screenModel.extractKeyText(
                                context = context,
                                onSuccess = { navigator.push(SummaryScreen(screenModel.resultText)) },
                                onFailure = {
                                    KToast.show(
                                        context = context,
                                        text = "Failed to extract summary: $it",
                                    )
                                }
                            )
                        },
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

//            if (screenModel.resultText.isNotEmpty()) {
//                item {
//                    ResultPreviewWithMarkdown(
//                        title = "Extracted summary",
//                        body = screenModel.resultText
//                    )
//                }
//            }

            if (screenModel.cards.isNotEmpty() && headingTitle != null) {
                item {
                    FlashcardItem(
                        set = FlashcardSet(
                            title = headingTitle,
                            count = screenModel.cards.size,
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            navigator.push(FlashCardScreen(screenModel.cards))
                        }
                    )
                }
            }

        }
    }
}
