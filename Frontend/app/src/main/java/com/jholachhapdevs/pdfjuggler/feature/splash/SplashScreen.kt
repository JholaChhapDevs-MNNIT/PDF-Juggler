package com.jholachhapdevs.pdfjuggler.feature.splash

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.kanhaji.basics.datastore.PrefsManager
import com.kanhaji.basics.datastore.PrefsResources
import com.jholachhapdevs.pdfjuggler.feature.home.HomeScreen
import com.jholachhapdevs.pdfjuggler.feature.login.LoginScreen
import kotlinx.coroutines.delay

object SplashScreen : Screen {
    private fun readResolve(): Any = SplashScreen

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            PrefsManager.init(context.applicationContext)
            delay(1000)
            val isLoggedIn = PrefsManager.getBoolean(PrefsResources.IS_LOGGED_IN) ?: false
            if (isLoggedIn) {
                navigator.replaceAll(HomeScreen)
            } else {
                navigator.replaceAll(LoginScreen)
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "PDF Juggler",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                LoadingIndicator()
            }
        }
    }
}