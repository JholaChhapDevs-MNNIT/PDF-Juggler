package com.jholachhapdevs.pdfjuggler.feature.flashcards.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jholachhapdevs.pdfjuggler.core.components.KAppBar
import com.jholachhapdevs.pdfjuggler.feature.home.ui.components.SwipeableCard
import kotlin.math.abs

@Composable
fun FlashCardComponent(
    screenModel: FlashCardScreenModel
) {
    Scaffold(
        topBar = { 
            KAppBar(
                title = "Flashcards",
                showSettingsIcon = false
            )
        }
    ) { innerPadding ->
        if (screenModel.cards.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No flashcards available",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // Flashcards content - stacked cards
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Title
                Text(
                    text = "Flashcards (${screenModel.cards.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                
                // Maintain a local deck so swipes can reorder cards without mutating screenModel state
                var deck by remember(screenModel.cards) { mutableStateOf(screenModel.cards.reversed()) }
                
                // Color palette for cards
                val palette = remember {
                    listOf(
                        Color(0xff90caf9), // Light Blue
                        Color(0xfffafafa), // Light Gray
                        Color(0xffef9a9a), // Light Red
                        Color(0xfffff59d), // Light Yellow
                        Color(0xffc5e1a5), // Light Green
                        Color(0xffce93d8)  // Light Purple
                    )
                }
                
                // Stacked flashcards
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    deck.forEachIndexed { index, card ->
                        key(card.front + "|" + card.back) {
                            val cardColor = palette[abs((card.front + "|" + card.back).hashCode()) % palette.size]
                            SwipeableCard(
                                order = index,
                                totalCount = deck.size,
                                backgroundColor = cardColor,
                                title = "Question",
                                text = card.front,
                                backTitle = "Answer",
                                backText = card.back,
                                canFlip = true,
                                stacked = true
                            ) {
                                // Move swiped card to the back of the deck
                                deck = listOf(card) + (deck - card)
                            }
                        }
                    }
                }
            }
        }
    }
}
