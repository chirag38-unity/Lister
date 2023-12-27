package com.chirag_redij.lister.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.chirag_redij.lister.presentation.lists.ListItem
import timber.log.Timber

@Composable
fun ListItemComposable(
    listItem: ListItem,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 10.dp,
    cutCournerRadius: Dp = 30.dp,
    onCheckClicked: (ListItem) -> Unit
) {

    Timber.tag("item").d("item id ->%s", listItem.id)

    LaunchedEffect(key1 = listItem.isDone) {
        Timber.tag("item").d("isDone ->" + listItem.isDone)
    }

    LaunchedEffect(key1 = listItem.timeInMillis) {
        Timber.tag("item").d("isDone ->" + listItem.timeInMillis)
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier.matchParentSize(),
        ) {
            val clipPath = Path().apply {
                lineTo(size.width - cutCournerRadius.toPx(), 0f)
                lineTo(size.width, cutCournerRadius.toPx())
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
                    topLeft = Offset(size.width - cutCournerRadius.toPx(), -100f),
                    size = Size(cutCournerRadius.toPx() + 100f, cutCournerRadius.toPx() + 100f),
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
                text = listItem.title,
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