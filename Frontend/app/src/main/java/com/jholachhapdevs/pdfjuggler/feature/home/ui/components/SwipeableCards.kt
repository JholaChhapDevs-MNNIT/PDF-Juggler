package com.jholachhapdevs.pdfjuggler.feature.home.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.clickable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Preview
@Composable
fun SwipeableCards() {
    var colors by remember {
        mutableStateOf(
            listOf(
                Color(0xff90caf9),
                Color(0xfffafafa),
                Color(0xffef9a9a),
                Color(0xfffff59d),
            ).reversed()
        )
    }

    Box(
        Modifier
            .background(Color.Black)
            .padding(vertical = 32.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        colors.forEachIndexed { idx, color ->
            key(color) {
                SwipeableCard(order = idx,
                    totalCount = colors.size,
                    backgroundColor = color,
                    onMoveToBack = {
                        colors = listOf(color) + (colors - color)
                    })
            }
        }
    }
}

@Composable
fun SwipeableCard(
    order: Int,
    totalCount: Int,
    backgroundColor: Color = Color.White,
    title: String? = null,
    text: String? = null,
    backTitle: String? = null,
    backText: String? = null,
    canFlip: Boolean = true,
    resetFlipSignal: Int = 0,
    stacked: Boolean = true,
    onMoveToBack: () -> Unit
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (stacked) 1f - (totalCount - order) * 0.05f else 1f,
    )
    val animatedYOffset by animateDpAsState(
        targetValue = if (stacked) ((totalCount - order) * -12).dp else 0.dp,
    )

    Box(
        modifier = Modifier
            .offset { IntOffset(x = 0, y = animatedYOffset.roundToPx()) }
            .graphicsLayer {
                scaleX = animatedScale
                scaleY = animatedScale
            }
    ) {
        var flipped by remember { mutableStateOf(false) }
        Box(
            Modifier.swipeToBack {
                flipped = false
                onMoveToBack()
            }
        ) {
            LaunchedEffect(resetFlipSignal) {
                // Reset flip when signaled from outside (e.g., programmatic toss)
                flipped = false
            }
            SampleCard(
                backgroundColor = backgroundColor,
                title = title,
                text = text,
                backTitle = backTitle,
                backText = backText,
                flipped = flipped,
                onToggleFlip = { if (canFlip) flipped = !flipped },
                canFlip = canFlip
            )
        }
    }
}

@Composable
fun SampleCard(
    backgroundColor: Color = Color.White,
    title: String? = null,
    text: String? = null,
    backTitle: String? = null,
    backText: String? = null,
    flipped: Boolean,
    onToggleFlip: () -> Unit,
    canFlip: Boolean = true
) {
    val rot by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 300)
    )

    Card(
        modifier = Modifier
            .height(220.dp)
            .fillMaxWidth(.8f)
            .graphicsLayer {
                rotationY = rot
                cameraDistance = 20f * density
            },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 32.dp)
        ) {
            val showingBack = rot > 90f
            val faceFix = if (showingBack) Modifier.graphicsLayer { rotationY = 180f } else Modifier

            val topContentModifier = Modifier
                .align(Alignment.TopCenter)
                .then(faceFix)

            Column(
                topContentModifier,
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val useTitle = if (showingBack) backTitle ?: title else title
                val useText = if (showingBack) backText ?: text else text
                if (useTitle != null || useText != null) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (useTitle != null) {
                            BasicText(
                                text = useTitle,
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 1
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        if (useText != null) {
                            BasicText(
                                text = useText,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                ),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            if (canFlip) {
                BasicText(
                    text = if (flipped) "Show Question" else "Show Answer",
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .then(faceFix)
                        .clickable { onToggleFlip() },
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.Black)
                )
            }
        }
    }
}

fun Modifier.swipeToBack(
    onMoveToBack: () -> Unit
): Modifier = composed {
    val offsetY = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    var leftSide by remember { mutableStateOf(true) }
    var clearedHurdle by remember { mutableStateOf(false) }

    pointerInput(Unit) {
        val decay = splineBasedDecay<Float>(this)

        coroutineScope {
            while (true) {
                offsetY.stop()
                val velocityTracker = VelocityTracker()

                awaitPointerEventScope {
                    verticalDrag(awaitFirstDown().id) { change ->
                        val verticalDragOffset = offsetY.value + change.positionChange().y
                        val horizontalPosition = change.previousPosition.x

                        leftSide = horizontalPosition <= size.width / 2
                        val offsetXRatioFromMiddle = if (leftSide) {
                            horizontalPosition / (size.width / 2)
                        } else {
                            (size.width - horizontalPosition) / (size.width / 2)
                        }
                        val rotationalOffset = max(1f, (1f - offsetXRatioFromMiddle) * 4f)

                        launch {
                            offsetY.snapTo(verticalDragOffset)
                            rotation.snapTo(if (leftSide) rotationalOffset else -rotationalOffset)
                        }

                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        if (change.positionChange() != Offset.Zero) change.consume()
                    }
                }

                val velocity = velocityTracker.calculateVelocity().y
                val targetOffsetY = decay.calculateTargetValue(offsetY.value, velocity)

                if (targetOffsetY.absoluteValue <= size.height) {
                    // Not enough velocity; Reset.
                    launch { offsetY.animateTo(targetValue = 0f, initialVelocity = velocity) }
                    launch { rotation.animateTo(targetValue = 0f, initialVelocity = velocity) }
                } else {
                    // Enough velocity to fling the card to the back
                    val boomerangDuration = 600
                    val maxDistanceToFling = (size.height * 4).toFloat()
                    val maxRotations = 3
                    val easeInOutEasing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)

                    val distanceToFling = min(
                        targetOffsetY.absoluteValue + size.height, maxDistanceToFling
                    )
                    val rotationToFling = min(
                        360f * (targetOffsetY.absoluteValue / size.height).roundToInt(),
                        360f * maxRotations
                    )
                    val rotationOvershoot = rotationToFling + 12f

                    val animationJobs = listOf(
                        launch {
                            rotation.animateTo(targetValue = if (leftSide) rotationToFling else -rotationToFling,
                                initialVelocity = velocity,
                                animationSpec = keyframes {
                                    durationMillis = boomerangDuration
                                    0f at 0 with easeInOutEasing
                                    (if (leftSide) rotationOvershoot else -rotationOvershoot) at boomerangDuration - 50 with LinearOutSlowInEasing
                                    (if (leftSide) rotationToFling else -rotationToFling) at boomerangDuration
                                })
                            rotation.snapTo(0f)
                        },
                        launch {
                            offsetY.animateTo(targetValue = 0f,
                                initialVelocity = velocity,
                                animationSpec = keyframes {
                                    durationMillis = boomerangDuration
                                    -distanceToFling at (boomerangDuration / 2) with easeInOutEasing
                                    40f at boomerangDuration - 70
                                }
                            ) {
                                if (value <= -size.height * 2 && !clearedHurdle) {
                                    onMoveToBack()
                                    clearedHurdle = true
                                }
                            }
                        }
                    )
                    animationJobs.joinAll()
                    clearedHurdle = false
                }
            }
        }
    }
        .offset { IntOffset(0, offsetY.value.roundToInt()) }
        .graphicsLayer {
            transformOrigin = TransformOrigin.Center
            rotationZ = rotation.value
        }
}

