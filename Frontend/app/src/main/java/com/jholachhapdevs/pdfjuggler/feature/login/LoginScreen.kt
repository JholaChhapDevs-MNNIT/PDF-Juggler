package com.jholachhapdevs.pdfjuggler.feature.login

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen

object LoginScreen: Screen {
    private fun readResolve(): Any = LoginScreen

    @Composable
    override fun Content() {

        val screenModel = rememberScreenModel {
            LoginScreenModel()
        }

        LoginComponent(screenModel)
    }
}