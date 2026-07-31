package com.nathan.selah

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconShield(modifier: Modifier = Modifier, tint: Color = SelahAccent, size: Dp = 24.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            lineTo(w * 0.88f, h * 0.22f)
            lineTo(w * 0.88f, h * 0.52f)
            cubicTo(w * 0.88f, h * 0.78f, w * 0.65f, h * 0.92f, w * 0.5f, h * 0.98f)
            cubicTo(w * 0.35f, h * 0.92f, w * 0.12f, h * 0.78f, w * 0.12f, h * 0.52f)
            lineTo(w * 0.12f, h * 0.22f)
            close()
        }
        drawPath(path = path, color = tint)
    }
}

@Composable
fun IconHome(modifier: Modifier = Modifier, tint: Color = SelahAccent, size: Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.1f)
            lineTo(w * 0.9f, h * 0.45f)
            lineTo(w * 0.78f, h * 0.45f)
            lineTo(w * 0.78f, h * 0.9f)
            lineTo(w * 0.22f, h * 0.9f)
            lineTo(w * 0.22f, h * 0.45f)
            lineTo(w * 0.1f, h * 0.45f)
            close()
        }
        drawPath(path = path, color = tint)
    }
}

@Composable
fun IconBook(modifier: Modifier = Modifier, tint: Color = SelahAccent, size: Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val path = Path().apply {
            moveTo(w * 0.15f, h * 0.2f)
            lineTo(w * 0.5f, h * 0.3f)
            lineTo(w * 0.85f, h * 0.2f)
            lineTo(w * 0.85f, h * 0.85f)
            lineTo(w * 0.5f, h * 0.92f)
            lineTo(w * 0.15f, h * 0.85f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = w * 0.08f))
    }
}

@Composable
fun IconSettings(modifier: Modifier = Modifier, tint: Color = SelahAccent, size: Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val stroke = w * 0.12f
        drawCircle(color = tint, radius = w * 0.28f, style = Stroke(width = stroke))
        drawCircle(color = tint, radius = w * 0.1f)
    }
}

@Composable
fun IconFlame(modifier: Modifier = Modifier, tint: Color = SelahAccent, size: Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            cubicTo(w * 0.7f, h * 0.35f, w * 0.9f, h * 0.55f, w * 0.9f, h * 0.72f)
            cubicTo(w * 0.9f, h * 0.9f, w * 0.72f, h * 0.98f, w * 0.5f, h * 0.98f)
            cubicTo(w * 0.28f, h * 0.98f, w * 0.1f, h * 0.9f, w * 0.1f, h * 0.72f)
            cubicTo(w * 0.1f, h * 0.5f, w * 0.35f, h * 0.3f, w * 0.5f, h * 0.08f)
            close()
        }
        drawPath(path = path, color = tint)
    }
}

@Composable
fun IconPause(modifier: Modifier = Modifier, tint: Color = SelahAccent, size: Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        drawRect(color = tint, topLeft = androidx.compose.ui.geometry.Offset(w * 0.22f, h * 0.15f), size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.7f))
        drawRect(color = tint, topLeft = androidx.compose.ui.geometry.Offset(w * 0.58f, h * 0.15f), size = androidx.compose.ui.geometry.Size(w * 0.2f, h * 0.7f))
    }
}

@Composable
fun IconHourglass(modifier: Modifier = Modifier, tint: Color = SelahAccent, size: Dp = 20.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.15f)
            lineTo(w * 0.8f, h * 0.15f)
            lineTo(w * 0.5f, h * 0.5f)
            lineTo(w * 0.8f, h * 0.85f)
            lineTo(w * 0.2f, h * 0.85f)
            lineTo(w * 0.5f, h * 0.5f)
            close()
        }
        drawPath(path = path, color = tint, style = Stroke(width = w * 0.08f))
    }
}

@Composable
fun IconSearch(modifier: Modifier = Modifier, tint: Color = SelahMuted, size: Dp = 16.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        drawCircle(color = tint, radius = w * 0.35f, center = androidx.compose.ui.geometry.Offset(w * 0.4f, h * 0.4f), style = Stroke(width = w * 0.12f))
        drawLine(color = tint, start = androidx.compose.ui.geometry.Offset(w * 0.65f, h * 0.65f), end = androidx.compose.ui.geometry.Offset(w * 0.95f, h * 0.95f), strokeWidth = w * 0.12f)
    }
}

@Composable
fun IconCheck(modifier: Modifier = Modifier, tint: Color = SelahBackground, size: Dp = 14.dp) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val path = Path().apply {
            moveTo(w * 0.2f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.75f)
            lineTo(w * 0.85f, h * 0.25f)
        }
        drawPath(path = path, color = tint, style = Stroke(width = w * 0.16f))
    }
}
