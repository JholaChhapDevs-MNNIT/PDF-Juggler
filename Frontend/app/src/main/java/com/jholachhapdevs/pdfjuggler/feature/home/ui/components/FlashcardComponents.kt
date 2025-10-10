package com.jholachhapdevs.pdfjuggler.feature.home.ui.components

//import androidx.compose.animation.core.Animatable
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.gestures.detectDragGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.offset
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.BasicText
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Surface
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateListOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.graphicsLayer
//import androidx.compose.ui.input.pointer.PointerInputChange
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.text.style.TextAlign
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import com.jholachhapdevs.pdfjuggler.feature.flashcards.domain.model.Flashcard
//import kotlinx.coroutines.launch
//import kotlin.math.abs
//import androidx.compose.animation.core.CubicBezierEasing
//import androidx.compose.animation.core.LinearOutSlowInEasing
//import androidx.compose.animation.core.keyframes
//import androidx.compose.animation.core.calculateTargetValue
//import androidx.compose.animation.splineBasedDecay
//import androidx.compose.foundation.gestures.awaitFirstDown
//import androidx.compose.foundation.gestures.verticalDrag
//import androidx.compose.ui.composed
//import androidx.compose.ui.graphics.TransformOrigin
//import androidx.compose.ui.input.pointer.positionChange
//import androidx.compose.ui.input.pointer.util.VelocityTracker
//import androidx.compose.ui.unit.IntOffset
//import kotlin.math.max
//import kotlin.math.min
//import kotlin.math.roundToInt
//import kotlinx.coroutines.joinAll
//import kotlin.math.absoluteValue
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.setValue
//
//@Composable
//fun FlashcardStack(cards: List<Flashcard>, cardHeight: Int = 300) {
//    val deck = remember(cards) { mutableStateListOf<Flashcard>().apply { addAll(cards) } }
//
//    // Re-sync deck when upstream cards change
//    LaunchedEffect(cards) {
//        deck.clear(); deck.addAll(cards)
//    }
//
//    val maxVisible = 4
//    Box(Modifier.fillMaxWidth()) {
//        val visible = deck.take(maxVisible)
//        visible.reversed().forEachIndexed { index, card ->
//            val layerIndex = visible.lastIndex - index // 0 is top, last is bottom (due to reversed drawing order)
//            val offsetY = (layerIndex * 14).dp
//            val scale = 1f - (layerIndex * 0.03f)
//            val isTop = layerIndex == 0
//            if (isTop) {
//                SwipableFlipCard(
//                    card = card,
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .offset(y = offsetY)
//                        .fillMaxWidth()
//                        .height(cardHeight.dp),
//                    scale = scale,
//                    onSwipedToBack = {
//                        // Move the first card to the back
//                        if (deck.isNotEmpty()) {
//                            val first = deck.removeAt(0)
//                            deck.add(first)
//                        }
//                    }
//                )
//            } else {
//                StaticFlipCard(
//                    card = card,
//                    modifier = Modifier
//                        .align(Alignment.TopCenter)
//                        .offset(y = offsetY)
//                        .fillMaxWidth()
//                        .height(cardHeight.dp),
//                    scale = scale
//                )
//            }
//        }
//    }
//}
//
//private fun Modifier.swipeToBack(
//    onMoveToBack: () -> Unit
//): Modifier = composed {
//    val offsetY = remember { Animatable(0f) }
//    val rotation = remember { Animatable(0f) }
//    var leftSide by remember { androidx.compose.runtime.mutableStateOf(true) }
//    var clearedHurdle by remember { androidx.compose.runtime.mutableStateOf(false) }
//
//    this
//        .pointerInput(Unit) {
//            val decay = splineBasedDecay<Float>(this)
//            kotlinx.coroutines.coroutineScope {
//                while (true) {
//                    offsetY.stop()
//                    val velocityTracker = VelocityTracker()
//
//                    awaitPointerEventScope {
//                        verticalDrag(awaitFirstDown().id) { change ->
//                            val verticalDragOffset = offsetY.value + change.positionChange().y
//                            val horizontalPosition = change.previousPosition.x
//
//                            leftSide = horizontalPosition <= size.width / 2
//                            val offsetXRatioFromMiddle = if (leftSide) {
//                                horizontalPosition / (size.width / 2)
//                            } else {
//                                (size.width - horizontalPosition) / (size.width / 2)
//                            }
//                            val rotationalOffset = max(1f, (1f - offsetXRatioFromMiddle) * 4f)
//
//                            launch {
//                                offsetY.snapTo(verticalDragOffset)
//                                rotation.snapTo(if (leftSide) rotationalOffset else -rotationalOffset)
//                            }
//
//                            velocityTracker.addPosition(change.uptimeMillis, change.position)
//                            if (change.positionChange() != Offset.Zero) change.consume()
//                        }
//                    }
//
//                    val velocity = velocityTracker.calculateVelocity().y
//                    val targetOffsetY = decay.calculateTargetValue(offsetY.value, velocity)
//
//                    if (targetOffsetY.absoluteValue <= size.height) {
//                        launch { offsetY.animateTo(targetValue = 0f, initialVelocity = velocity) }
//                        launch { rotation.animateTo(targetValue = 0f, initialVelocity = velocity) }
//                    } else {
//                        val boomerangDuration = 600
//                        val maxDistanceToFling = (size.height * 4).toFloat()
//                        val maxRotations = 3
//                        val easeInOutEasing = CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)
//
//                        val distanceToFling = min(
//                            targetOffsetY.absoluteValue + size.height, maxDistanceToFling
//                        )
//                        val rotationToFling = min(
//                            360f * (targetOffsetY.absoluteValue / size.height).roundToInt(),
//                            360f * maxRotations
//                        )
//                        val rotationOvershoot = rotationToFling + 12f
//
//                        val animationJobs = listOf(
//                            launch {
//                                rotation.animateTo(targetValue = if (leftSide) rotationToFling else -rotationToFling,
//                                    initialVelocity = velocity,
//                                    animationSpec = keyframes {
//                                        durationMillis = boomerangDuration
//                                        0f at 0 with easeInOutEasing
//                                        (if (leftSide) rotationOvershoot else -rotationOvershoot) at boomerangDuration - 50 with LinearOutSlowInEasing
//                                        (if (leftSide) rotationToFling else -rotationToFling) at boomerangDuration
//                                    })
//                                rotation.snapTo(0f)
//                            },
//                            launch {
//                                offsetY.animateTo(targetValue = 0f,
//                                    initialVelocity = velocity,
//                                    animationSpec = keyframes {
//                                        durationMillis = boomerangDuration
//                                        -distanceToFling at (boomerangDuration / 2) with easeInOutEasing
//                                        40f at boomerangDuration - 70
//                                    }
//                                ) {
//                                    if (value <= -size.height * 2 && !clearedHurdle) {
//                                        onMoveToBack()
//                                        clearedHurdle = true
//                                    }
//                                }
//                            }
//                        )
//                        animationJobs.joinAll()
//                        clearedHurdle = false
//                    }
//                }
//            }
//        }
//        .offset { IntOffset(0, offsetY.value.roundToInt()) }
//        .graphicsLayer {
//            transformOrigin = TransformOrigin.Center
//            rotationZ = rotation.value
//        }
//}
//
//@Composable
//private fun StaticFlipCard(card: Flashcard, modifier: Modifier = Modifier, scale: Float = 1f) {
//    FlipCard(
//        front = card.front,
//        back = card.back,
//        modifier = modifier.graphicsLayer { this.scaleX = scale; this.scaleY = scale }
//    )
//}
//
//@Composable
//private fun SwipableFlipCard(
//    card: Flashcard,
//    modifier: Modifier = Modifier,
//    scale: Float = 1f,
//    onSwipedToBack: () -> Unit
//) {
//    FlipCard(
//        front = card.front,
//        back = card.back,
//        modifier = modifier
//            .graphicsLayer {
//                this.scaleX = scale
//                this.scaleY = scale
//            }
//            .swipeToBack { onSwipedToBack() }
//    )
//}
//
//@Composable
//private fun FlipCard(
//    front: String,
//    back: String,
//    modifier: Modifier = Modifier
//) {
//    val cs = MaterialTheme.colorScheme
//    val shape = RoundedCornerShape(18.dp)
//    val flipped = remember { androidx.compose.runtime.mutableStateOf(false) }
//    val rot = androidx.compose.animation.core.animateFloatAsState(if (flipped.value) 180f else 0f, label = "flip")
//
//    Surface(
//        color = cs.surface,
//        tonalElevation = 3.dp,
//        shadowElevation = 10.dp,
//        border = BorderStroke(1.dp, cs.outlineVariant),
//        shape = shape,
//        modifier = modifier
//            .graphicsLayer {
//                rotationY = rot.value
//                cameraDistance = 20f * density
//            }
//            .clickable { flipped.value = !flipped.value }
//    ) {
//        val showingBack = rot.value > 90f
//        val faceFix = if (showingBack) Modifier.graphicsLayer { rotationY = 180f } else Modifier
//        Surface(
//            color = if (showingBack) cs.secondaryContainer else cs.primaryContainer,
//            contentColor = if (showingBack) cs.onSecondaryContainer else cs.onPrimaryContainer,
//            shape = shape,
//            modifier = faceFix.fillMaxWidth()
//        ) {
//            Column(
//                Modifier.padding(20.dp),
//                verticalArrangement = Arrangement.spacedBy(12.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                BasicText(
//                    text = if (showingBack) "Answer" else "Question",
//                    maxLines = 1,
//                    style = MaterialTheme.typography.headlineSmall.copy(textAlign = TextAlign.Center)
//                )
//                BasicText(
//                    text = if (showingBack) back else front,
//                    maxLines = 3,
//                    style = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
//                    overflow = TextOverflow.Ellipsis
//                )
//                BasicText(
//                    text = "Tap to flip • Drag to stack",
//                    maxLines = 1,
//                    style = MaterialTheme.typography.bodySmall.copy(textAlign = TextAlign.Center),
//                    overflow = TextOverflow.Clip
//                )
//            }
//        }
//    }
//}
