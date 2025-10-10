package com.jholachhapdevs.pdfjuggler.feature.home.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.jholachhapdevs.pdfjuggler.feature.home.ui.components.HomeScreen

object HomeScreen: Screen {

    private fun readResolve(): Any = HomeScreen

    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel {
            HomeScreenModel()
        }
//        HomeComponent(screenModel)
        HomeScreen(screenModel)
    }
}