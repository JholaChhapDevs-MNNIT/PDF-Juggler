package com.jholachhapdevs.pdfjuggler.core.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KAppBar(
    title: String = "Pdf Juggler",
    showSettingsIcon: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val navigator = LocalNavigator.currentOrThrow
    TopAppBar(
        title = {
            Text(title)
        },
        actions = {
            NavigationActions(
                navigator = navigator,
                showSettingsIcon = showSettingsIcon
            ) {
                actions()
            }
        },
        navigationIcon = {
            BackNavigationIcon(navigator)
        }
    )
}