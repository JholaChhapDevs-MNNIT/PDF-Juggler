package com.jholachhapdevs.pdfjuggler.feature.home.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(onImportClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                "Fortune-Deck",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
//        actions = {
//            IconButton(onClick = onImportClick) {
//                Icon(
//                    imageVector = Icons.Outlined.Add,
//                    contentDescription = "Add",
//                    tint = MaterialTheme.colorScheme.onPrimaryContainer
//                )
//            }
//        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
