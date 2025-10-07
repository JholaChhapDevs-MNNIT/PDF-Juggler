package com.jholachhapdevs.pdfjuggler.feature.login.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

enum class OtpStatus { Idle, Success, Error }

@Composable
fun OtpInput(
    otpLength: Int = 6,
    spacing: Dp = 8.dp,
    onOtpComplete: (String, (OtpStatus) -> Unit) -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = "", selection = TextRange(0)))
    }
    val digits = textFieldValue.text
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var hiddenFieldFocused by remember { mutableStateOf(false) }

    var otpStatus by remember { mutableStateOf(OtpStatus.Idle) }

    val successHighlights = remember { List(otpLength) { Animatable(0f) } }
    val successScales = remember { List(otpLength) { Animatable(1f) } }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val activeBorderColor = MaterialTheme.colorScheme.tertiary
    val activeBackgroundColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
    val filledBorderColor = MaterialTheme.colorScheme.primary
    val inactiveBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)

    // When full, highlight only if the user explicitly tapped a cell
    var forceHighlight by remember { mutableStateOf(false) }

    val caretIndex = run {
        val sel = textFieldValue.selection.start
        if (digits.length == otpLength) sel.coerceIn(0, otpLength - 1)
        else sel.coerceIn(0, digits.length)
    }

    // Hide by default when full; show if user tapped a cell while full
    val showActiveHighlight = hiddenFieldFocused && (digits.length < otpLength || forceHighlight)

    Row(
        horizontalArrangement = Arrangement.spacedBy(spacing),
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = shakeOffset.value.dp),
    ) {
        for (i in 0 until otpLength) {
            val char = if (i < digits.length) digits[i].toString() else ""
            val isActive = showActiveHighlight && i == caretIndex
            val isFilled = i < digits.length

            val highlightProgress = successHighlights[i].value
            val scaleAnim = successScales[i].value

            val targetColor = when {
                otpStatus == OtpStatus.Success && highlightProgress > 0f -> Color.Green
                otpStatus == OtpStatus.Error -> Color.Red
                isActive -> activeBorderColor
                isFilled -> filledBorderColor
                else -> inactiveBorderColor
            }

            val animatedBorderColor by animateColorAsState(
                targetValue = targetColor,
                animationSpec = tween(durationMillis = 300),
                label = "borderColorAnim"
            )

            OutlinedCard(
                onClick = {
                    val newPos = min(i, digits.length)
                    textFieldValue = TextFieldValue(digits, TextRange(newPos))
                    // If full, explicitly show highlight for the tapped cell
                    forceHighlight = digits.length == otpLength
                    focusRequester.requestFocus()
                    // Ensure IME shows on any tap
                    keyboardController?.show()
                },
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    width = if (isActive) 2.dp else 1.5.dp,
                    color = animatedBorderColor
                ),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (isActive) activeBackgroundColor else Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .scale(scaleAnim)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char,
                        style = TextStyle(
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = if (isFilled) FontWeight.Medium else FontWeight.Normal
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = { incoming ->
            val filtered = incoming.text.filter { it.isDigit() }
            val clipped = if (filtered.length > otpLength) filtered.take(otpLength) else filtered

            val caretInIncoming = incoming.selection.start.coerceIn(0, incoming.text.length)
            val digitsBeforeCaret = incoming.text.take(caretInIncoming).count { it.isDigit() }
            val newCaret = min(digitsBeforeCaret, clipped.length)

            val wasComplete = textFieldValue.text.length == otpLength
            val nowComplete = clipped.length == otpLength

            textFieldValue = TextFieldValue(
                text = clipped,
                selection = TextRange(newCaret)
            )

            // Reset forced highlight on any edit that makes it not full,
            // and also when it becomes full by typing (keep the "illusion").
            if (!nowComplete) forceHighlight = false
            if (nowComplete && !wasComplete) forceHighlight = false

            if (nowComplete && !wasComplete) {
                onOtpComplete(clipped) { status ->
                    otpStatus = status
                    scope.launch {
                        when (status) {
                            OtpStatus.Success -> {
                                for (i in 0 until otpLength) {
                                    successHighlights[i].snapTo(0f)
                                    successScales[i].snapTo(1f)
                                }
                                for (i in 0 until otpLength) {
                                    launch {
                                        successHighlights[i].animateTo(1f, tween(150))
                                        successScales[i].animateTo(1.05f, tween(100))
                                        successScales[i].animateTo(1f, tween(100))
                                    }
                                    delay(40)
                                }
                                delay(400)
                                otpStatus = OtpStatus.Idle
                            }
                            OtpStatus.Error -> {
                                repeat(2) {
                                    shakeOffset.animateTo(-10f, tween(50))
                                    shakeOffset.animateTo(10f, tween(50))
                                }
                                shakeOffset.animateTo(0f, tween(50))
                                delay(600)
                                otpStatus = OtpStatus.Idle
                            }
                            else -> Unit
                        }
                    }
                }
            }
        },
        modifier = Modifier
            .size(1.dp)
            .alpha(0f)
            .focusRequester(focusRequester)
            .onFocusChanged { state ->
                hiddenFieldFocused = state.isFocused
                if (!state.isFocused) forceHighlight = false
            }
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown && event.key == Key.Backspace) {
                    val sel = textFieldValue.selection
                    if (sel.collapsed && sel.start == 0 && digits.isNotEmpty()) {
                        val newText = digits.drop(1)
                        textFieldValue = TextFieldValue(text = newText, selection = TextRange(0))
                        // Now not full -> no need to force highlight
                        forceHighlight = false
                        true
                    } else false
                } else false
            },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        textStyle = TextStyle(color = Color.Transparent),
        cursorBrush = SolidColor(Color.Transparent),
        singleLine = true
    )

    LaunchedEffect(hiddenFieldFocused) {
        if (hiddenFieldFocused) keyboardController?.show()
    }
}

