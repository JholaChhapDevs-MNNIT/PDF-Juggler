package com.jholachhapdevs.pdfjuggler.feature.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.jholachhapdevs.pdfjuggler.feature.login.component.OtpInput
import com.jholachhapdevs.pdfjuggler.feature.login.component.OtpStatus
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginComponent(
    screenModel: LoginScreenModel
) {
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    var phone by remember { mutableStateOf("") }
    var isSending by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    var serverOtp by remember { mutableStateOf<String?>(null) }
    var infoMessage by remember { mutableStateOf<String?>(null) }

    fun isValidPhone(p: String): Boolean {
        // Simple 10-digit validation (digits only)
        return p.length == 10 && p.all { it.isDigit() }
    }

    LaunchedEffect(Unit) { infoMessage = null }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = phone,
            onValueChange = { new ->
                if (new.length <= 10 && new.all { it.isDigit() }) phone = new
            },
            label = { Text("Phone number") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (isValidPhone(phone) && !isSending) {
                        isSending = true
                        infoMessage = null
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
                                    infoMessage = err
                                }
                            )
                        }
                    } else if (!isValidPhone(phone)) {
                        infoMessage = "Enter a valid 10-digit phone number."
                    }
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = {
                focusManager.clearFocus()
                if (!isValidPhone(phone)) {
                    infoMessage = "Enter a valid 10-digit phone number."
                    return@OutlinedButton
                }
                isSending = true
                infoMessage = null
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
                            infoMessage = err
                        }
                    )
                }
            },
            enabled = !isSending && isValidPhone(phone)
        ) {
            if (isSending) {
                LoadingIndicator()
            } else {
                Text("Send OTP")
            }
        }

        if (infoMessage != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = infoMessage!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showOtpDialog) {
        AlertDialog(
            onDismissRequest = { showOtpDialog = false },
            title = { Text("Enter OTP") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("We sent a 6-digit code to $phone")
                    OtpInput(
                        otpLength = 6,
                        onOtpComplete = { entered, setStatus ->
                            val ok = serverOtp != null && entered == serverOtp
                            setStatus(if (ok) OtpStatus.Success else OtpStatus.Error)
                            if (ok) {
                                scope.launch {
                                    delay(600)
                                    showOtpDialog = false
                                    infoMessage = "Phone verified."
                                }
                                // TODO: Save to Prefs later
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