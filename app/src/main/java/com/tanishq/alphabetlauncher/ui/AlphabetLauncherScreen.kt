package com.tanishq.alphabetlauncher.ui

import android.graphics.drawable.Drawable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.exp
import kotlin.math.roundToInt

private val Letters = ('A'..'Z').toList()

@Composable
fun AlphabetLauncherScreen(
    viewModel: LauncherViewModel,
    onLaunchApp: (com.tanishq.alphabetlauncher.model.LaunchableApp) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    var fingerY by remember { mutableFloatStateOf(0f) }
    var selectedLetter by remember { mutableStateOf<Char?>(null) }

    val bend = remember { Animatable(0f) }
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val currentApps = selectedLetter?.let(viewModel::appsFor).orEmpty()

    val updateLetter = { y: Float, barTop: Float, barHeight: Float ->
        val normalized = ((y - barTop) / barHeight).coerceIn(0f, 0.99999f)
        val index = (normalized * Letters.size).toInt().coerceIn(0, Letters.lastIndex)
        selectedLetter = Letters[index]
        fingerY = y
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Main content
        if (!isDragging || selectedLetter == null) {
            HomeContent(
                apps = viewModel.favourites(),
                onLaunchApp = onLaunchApp
            )
        } else {
            FilteredContent(
                letter = selectedLetter!!,
                apps = currentApps,
                onLaunchApp = onLaunchApp
            )
        }

        // Right-side alphabet is its own touch surface so its geometry remains independent
        // from the rest of the screen.
        AlphabetBar(
            modifier = Modifier.align(Alignment.CenterEnd),
            isDragging = isDragging,
            fingerY = fingerY,
            bend = bend.value,
            hasApps = viewModel::hasApps,
            onTouch = { y, top, height ->
                isDragging = true
                updateLetter(y, top, height)
                scope.launch {
                    bend.snapTo(1f)
                }
            },
            onRelease = {
                isDragging = false
                selectedLetter = null
                scope.launch {
                    bend.animateTo(
                        0f,
                        spring(
                            dampingRatio = 0.72f,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                }
            }
        )
    }
}

@Composable
private fun HomeContent(
    apps: List<com.tanishq.alphabetlauncher.model.LaunchableApp>,
    onLaunchApp: (com.tanishq.alphabetlauncher.model.LaunchableApp) -> Unit
) {
    val now = remember { mutableStateOf(LocalDateTime.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            now.value = LocalDateTime.now()
            kotlinx.coroutines.delay(1000)
        }
    }

    val time = now.value.format(DateTimeFormatter.ofPattern("HH:mm"))
    val date = now.value.format(
        DateTimeFormatter.ofPattern("EEE dd MMM", Locale.ENGLISH)
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .padding(start = 28.dp, top = 0.dp, bottom = 20.dp)
            .padding(end = 72.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = time,
            color = Color.White,
            fontSize = 54.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = (-1.5).sp
        )
        Text(
            text = date,
            color = Color.White,
            fontSize = 17.sp,
            modifier = Modifier.padding(top = 2.dp, bottom = 22.dp)
        )

        apps.forEach { app ->
            AppRow(
                app = app,
                onClick = { onLaunchApp(app) }
            )
        }
    }
}

@Composable
private fun FilteredContent(
    letter: Char,
    apps: List<com.tanishq.alphabetlauncher.model.LaunchableApp>,
    onLaunchApp: (com.tanishq.alphabetlauncher.model.LaunchableApp) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 28.dp, top = 42.dp, end = 76.dp, bottom = 24.dp)
    ) {
        Text(
            text = letter.toString(),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (apps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No apps",
                    color = Color(0xFF888888),
                    fontSize = 16.sp
                )
            }
        } else {
            LazyColumn {
                items(
                    items = apps,
                    key = { "${it.packageName}:${it.activityName}" }
                ) { app ->
                    AppRow(
                        app = app,
                        onClick = { onLaunchApp(app) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppRow(
    app: com.tanishq.alphabetlauncher.model.LaunchableApp,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color.Transparent)
            .clickable(
                indication = null,
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DrawableIcon(
            drawable = app.icon,
            modifier = Modifier.size(34.dp)
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = app.label,
            color = Color.White,
            fontSize = 15.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun DrawableIcon(drawable: Drawable, modifier: Modifier = Modifier) {
    val bitmap = remember(drawable) {
        drawable.toBitmap(
            width = 96,
            height = 96,
            config = android.graphics.Bitmap.Config.ARGB_8888
        )
    }

    Image(
        bitmap = bitmap.asImageBitmap(),
        contentDescription = null,
        modifier = modifier.clip(CircleShape),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun AlphabetBar(
    modifier: Modifier = Modifier,
    isDragging: Boolean,
    fingerY: Float,
    bend: Float,
    hasApps: (Char) -> Boolean,
    onTouch: (y: Float, top: Float, height: Float) -> Unit,
    onRelease: () -> Unit
) {
    val density = LocalDensity.current
    val sidePadding = with(density) { 8.dp.toPx() }

    BoxWithTouch(
        modifier = modifier
            .width(72.dp)
            .fillMaxHeight()
            .padding(end = 4.dp),
        onTouch = onTouch,
        onRelease = onRelease
    ) { top, height ->
        Canvas(Modifier.fillMaxSize()) {
            val contentTop = 36.dp.toPx()
            val contentBottom = size.height - 46.dp.toPx()
            val letterAreaHeight = (contentBottom - contentTop).coerceAtLeast(1f)
            val step = letterAreaHeight / Letters.size
            val maxShift = size.width * 0.34f

            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = Offset(
                    x = size.width - sidePadding - 4.dp.toPx(),
                    y = contentBottom + 14.dp.toPx()
                )
            )

            // Star at the top, matching the reference layout.
            drawStar(
                center = Offset(
                    x = size.width - sidePadding - 4.dp.toPx(),
                    y = contentTop - 18.dp.toPx()
                ),
                outerRadius = 7.dp.toPx(),
                innerRadius = 3.dp.toPx(),
                color = Color.White
            )

            Letters.forEachIndexed { index, letter ->
                val y = contentTop + step * (index + 0.5f)

                val distance = if (isDragging) {
                    y - fingerY
                } else {
                    Float.POSITIVE_INFINITY
                }

                // Gaussian falloff:
                // letters nearest the finger move the most; distant letters barely move.
                val sigma = letterAreaHeight * 0.10f
                val influence = if (isDragging) {
                    exp(-(distance * distance) / (2f * sigma * sigma))
                } else {
                    0f
                }

                val x = size.width - sidePadding - 4.dp.toPx() - (maxShift * influence * bend)

                val selected = isDragging &&
                    kotlin.math.abs(y - fingerY) <= step * 0.50f

                drawTextCentered(
                    text = letter.toString(),
                    center = Offset(x, y),
                    sizeSp = if (selected) 17f else 13f,
                    color = if (hasApps(letter)) Color.White else Color(0xFF666666)
                )
            }

            if (isDragging) {
                val selected = selectedLetterFromPosition(
                    fingerY = fingerY,
                    top = top + contentTop,
                    height = letterAreaHeight
                )
                val index = Letters.indexOf(selected)
                val y = contentTop + step * (index + 0.5f)
                val distance = y - fingerY
                val sigma = letterAreaHeight * 0.10f
                val influence = exp(-(distance * distance) / (2f * sigma * sigma))
                val bubbleX = size.width - sidePadding - 4.dp.toPx() -
                    (maxShift * influence * bend) - 28.dp.toPx()

                drawCircle(
                    color = Color(0xFF1E1E1E),
                    radius = 18.dp.toPx(),
                    center = Offset(bubbleX, fingerY)
                )
                drawCircle(
                    color = Color(0xFF4A4A4A),
                    radius = 18.dp.toPx(),
                    center = Offset(bubbleX, fingerY),
                    style = Stroke(width = 1.dp.toPx())
                )
                drawTextCentered(
                    text = selected.toString(),
                    center = Offset(bubbleX, fingerY),
                    sizeSp = 15f,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun BoxWithTouch(
    modifier: Modifier,
    onTouch: (y: Float, top: Float, height: Float) -> Unit,
    onRelease: () -> Unit,
    content: @Composable BoxScope.(top: Float, height: Float) -> Unit
) {
    Box(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { offset ->
                    onTouch(offset.y, 0f, size.height.toFloat())
                },
                onDrag = { change, _ ->
                    change.consume()
                    onTouch(change.position.y, 0f, size.height.toFloat())
                },
                onDragEnd = { onRelease() },
                onDragCancel = { onRelease() }
            )
        }
    ) {
        content(0f, 0f)
    }
}

private fun selectedLetterFromPosition(
    fingerY: Float,
    top: Float,
    height: Float
): Char {
    val normalized = ((fingerY - top) / height).coerceIn(0f, 0.99999f)
    return Letters[(normalized * Letters.size).toInt().coerceIn(0, Letters.lastIndex)]
}

private fun DrawScope.drawTextCentered(
    text: String,
    center: Offset,
    sizeSp: Float,
    color: Color
) {
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color.toArgb()
        textSize = sizeSp * density
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.create("sans", android.graphics.Typeface.NORMAL)
    }

    drawContext.canvas.nativeCanvas.drawText(
        text,
        center.x,
        center.y - (paint.ascent() + paint.descent()) / 2f,
        paint
    )
}

private fun DrawScope.drawStar(
    center: Offset,
    outerRadius: Float,
    innerRadius: Float,
    color: Color
) {
    val path = android.graphics.Path()
    for (i in 0 until 10) {
        val angle = Math.toRadians(-90.0 + i * 36.0)
        val radius = if (i % 2 == 0) outerRadius else innerRadius
        val x = center.x + kotlin.math.cos(angle).toFloat() * radius
        val y = center.y + kotlin.math.sin(angle).toFloat() * radius
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawContext.canvas.nativeCanvas.drawPath(
        path,
        android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 1.6f * density
            this.color = color.toArgb()
        }
    )
}

