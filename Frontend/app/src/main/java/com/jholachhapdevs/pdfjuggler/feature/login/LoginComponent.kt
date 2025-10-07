package com.jholachhapdevs.pdfjuggler.feature.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.jholachhapdevs.pdfjuggler.feature.home.HomeScreen
import com.jholachhapdevs.pdfjuggler.feature.login.component.OtpInput
import com.jholachhapdevs.pdfjuggler.feature.login.component.OtpStatus
import com.kanhaji.basics.datastore.PrefsManager
import com.kanhaji.basics.datastore.PrefsResources
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun LoginComponent(
    screenModel: LoginScreenModel
) {
    val navigator = LocalNavigator.currentOrThrow
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var phone by rememberSaveable { mutableStateOf("") }
    var isSending by rememberSaveable { mutableStateOf(false) }
    var showOtpDialog by rememberSaveable { mutableStateOf(false) }
    var serverOtp by rememberSaveable { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun isValidPhone(p: String): Boolean = p.length == 10 && p.all { it.isDigit() }

    fun maskedPhone(p: String): String =
        if (p.length >= 4) "••••••${p.takeLast(4)}" else p

    fun sendOtp() {
        focusManager.clearFocus()
        errorMessage = null
        infoMessage = null

        if (!isValidPhone(phone)) {
            errorMessage = "Enter a valid 10‑digit phone number."
            return
        }
        if (isSending) return

        isSending = true
        scope.launch {
            screenModel.initiateOtpRequest(
                phoneNumber = phone,
                onSuccess = { resp ->
                    isSending = false
                    serverOtp = resp.OTP
                    showOtpDialog = true
                    infoMessage = "OTP sent."
                },
                onFailure = { err ->
                    isSending = false
                    errorMessage = err
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        infoMessage = null
        errorMessage = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Sign in",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = "Use your phone number to receive a one‑time code.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { new ->
                        if (new.length <= 10 && new.all { it.isDigit() }) {
                            phone = new
                            errorMessage = null
                        }
                    },
                    label = { Text("Phone number") },
                    singleLine = true,
                    isError = errorMessage != null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(onSend = { sendOtp() }),
                    supportingText = {
                        val helper = errorMessage ?: "Enter your 10‑digit phone number."
                        Text(
                            text = helper,
                            color = if (errorMessage != null)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(
                        onClick = { sendOtp() },
                        enabled = !isSending && isValidPhone(phone),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .align(Alignment.CenterVertically)
                            )
                        }
                        Text(if (isSending) "Sending..." else "Send OTP")
                    }
                }

                AnimatedVisibility(
                    visible = infoMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    infoMessage?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = { Text("Verify OTP") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "We sent a 6‑digit code to ${maskedPhone(phone)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OtpInput(
                        otpLength = 6,
                        onOtpComplete = { entered, setStatus ->
                            val ok = serverOtp != null && entered == serverOtp
                            setStatus(if (ok) OtpStatus.Success else OtpStatus.Error)
                            if (ok) {
                                scope.launch {
                                    delay(600)
                                    showOtpDialog = false
                                    navigator.replaceAll(HomeScreen)
                                    PrefsManager.saveBoolean(PrefsResources.IS_LOGGED_IN, true)
                                }
                                // TODO: Save to Prefs later (intentionally commented)
                                // PrefsManager.saveString(PrefsResources.PHONE_NUMBER, phone)
                                // PrefsManager.saveString(PrefsResources.TOKEN, "<token>")
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showOtpDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}