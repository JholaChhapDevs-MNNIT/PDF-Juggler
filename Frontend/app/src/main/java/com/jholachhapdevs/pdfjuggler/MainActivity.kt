package com.jholachhapdevs.pdfjuggler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.jholachhapdevs.pdfjuggler.core.ui.theme.PDFJugglerTheme
import com.jholachhapdevs.pdfjuggler.feature.login.LoginScreen
import com.jholachhapdevs.pdfjuggler.feature.splash.SplashScreen
import com.kanhaji.basics.datastore.PrefsManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        PrefsManager.init(this)
        setContent {
            PDFJugglerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Navigator(SplashScreen) { navigator ->
                            SlideTransition(navigator)
                        }
                    }
                }
            }
        }
    }
}