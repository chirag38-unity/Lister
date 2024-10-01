package com.chirag_redij.lister.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.chirag_redij.lister.presentation.notes.Note
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs

@Composable
fun ListItemComposable(
    listItem: Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCornerRadius: Dp = 30.dp,
    onDelete: (Note) -> Unit,
    onCheckClicked: (Note) -> Unit
) {

    val coroutineScope = rememberCoroutineScope()

    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = { state ->
            if (state == SwipeToDismissBoxValue.EndToStart) {
                coroutineScope.launch {
                    onDelete(listItem)
                }
                true
            } else {
                false
            }
        },
        positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold
    )

    SwipeToDismissBoxDefaults.positionalThreshold

    val progress = swipeToDismissBoxState.progress

    LaunchedEffect (progress) {
        Timber.tag("Progress").d("Progress -> $progress")
    }

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {

            val backGroundColor by animateColorAsState (
                targetValue = when (swipeToDismissBoxState.currentValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Color.Green
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> {
                        // Mix between White and the target colors based on swipe progress
                        val startColor = Color.White
                        val endColor = Color.Red

                        // Interpolating between the colors based on swipe progress
                        lerp(startColor, endColor, progress)
                    }

                }, label = "BG Color"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backGroundColor)
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = Color.White
                )
            }

        },
        modifier = modifier
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier.matchParentSize(),
            ) {
                val clipPath = Path().apply {
                    lineTo(size.width - cutCornerRadius.toPx(), 0f)
                    lineTo(size.width, cutCornerRadius.toPx())
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                clipPath(clipPath) {
                    drawRoundRect(
                        color = Color.Yellow,
                        size = size,
                        cornerRadius = CornerRadius(cornerRadius.toPx())
                    )
                    drawRoundRect(
                        color = Color(
                            ColorUtils.blendARGB(16770611, 0x000000, 0.1f)
                        ),
                        topLeft = Offset(size.width - cutCornerRadius.toPx(), -100f),
                        size = Size(cutCornerRadius.toPx() + 100f, cutCornerRadius.toPx() + 100f),
                        cornerRadius = CornerRadius(cornerRadius.toPx())
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(end = 32.dp)
            ) {
                Text(
                    text = listItem.title ?: "",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        textDecoration = if (listItem.isDone) TextDecoration.LineThrough else null
                    ),
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Checkbox(
                    checked = listItem.isDone,
                    onCheckedChange = {
                        onCheckClicked(listItem)
                    }
                )

            }
        }
    }


}

fun lerp(start: Color, end: Color, progress: Float): Color {
    val normalizedProgress = 1 - abs(progress)

    return Color(
        red = start.red + (end.red - start.red) * normalizedProgress,
        green = start.green + (end.green - start.green) * normalizedProgress,
        blue = start.blue + (end.blue - start.blue) * normalizedProgress,
        alpha = start.alpha + (end.alpha - start.alpha) * normalizedProgress
    )
}

@Composable
fun SwipeableListItemComposable(
    listItem: Note,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCornerRadius: Dp = 30.dp,
    onDelete: (Note) -> Unit,
    onCheckClicked: (Note) -> Unit
) {

    val bgColor = MaterialTheme.colorScheme.primaryContainer
    val oppBgColor = MaterialTheme.colorScheme.onPrimaryContainer

    SwipeableItemWithActions(
        actions = {
            ActionIcon(
                onClick = {
                    onDelete(listItem)
                },
                backgroundColor = Color.Transparent,
                tint = MaterialTheme.colorScheme.primary,
                icon = Icons.Default.Delete,
                modifier = Modifier.fillMaxHeight()
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Canvas(
                    modifier = Modifier.matchParentSize()
                        .background(Color.Transparent)
                ) {
                    val clipPath = Path().apply {
                        lineTo(size.width - cutCornerRadius.toPx(), 0f)
                        lineTo(size.width, cutCornerRadius.toPx())
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    clipPath(clipPath) {

                        drawRoundRect(
                            color = bgColor,
                            size = size,
                            cornerRadius = CornerRadius(cornerRadius.toPx())
                        )
                        drawRoundRect(
                            color = Color(
                                ColorUtils.blendARGB(
                                    bgColor.toArgb(),
                                    oppBgColor.toArgb(),
                                    0.2f)
                            ),
                            topLeft = Offset(size.width - cutCornerRadius.toPx(), -100f),
                            size = Size(cutCornerRadius.toPx() + 100f, cutCornerRadius.toPx() + 100f),
                            cornerRadius = CornerRadius(cornerRadius.toPx()),
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(end = 32.dp)
                ) {
                    Text(
                        text = listItem.title ?: "",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            textDecoration = if (listItem.isDone) TextDecoration.LineThrough else null
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Checkbox(
                        checked = listItem.isDone,
                        onCheckedChange = {
                            onCheckClicked(listItem)
                        }
                    )

                }
            }
        },
        modifier = modifier
    )
}