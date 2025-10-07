package com.jholachhapdevs.pdfjuggler.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun HomeComponent(
    screenModel: HomeScreenModel
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(onClick = { screenModel.pickPdf(context) }) {
            Text("Pick PDF")
        }

        Spacer(modifier = Modifier.height(16.dp))

        screenModel.selectedPdfUri?.let { uri ->
            Text(
                text = "Selected: $uri",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}