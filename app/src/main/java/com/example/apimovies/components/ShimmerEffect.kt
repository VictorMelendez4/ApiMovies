package com.example.apimovies.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.apimovies.ui.theme.SecondaryDark

@Composable
fun ShimmerLoadingAnimation() {
    val shimmerColors = listOf(
        SecondaryDark.copy(alpha = 0.6f),
        SecondaryDark.copy(alpha = 0.2f),
        SecondaryDark.copy(alpha = 0.6f),
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    LazyColumn(contentPadding = PaddingValues(10.dp)) {
        items(6) {
            ShimmerMovieCardItem(brush = brush)
        }
    }
}

@Composable
fun ShimmerMovieCardItem(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .size(width = 90.dp, height = 120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(brush)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Spacer(modifier = Modifier.height(20.dp).fillMaxWidth(0.7f).clip(RoundedCornerShape(4.dp)).background(brush))
            Spacer(modifier = Modifier.height(10.dp))
            Spacer(modifier = Modifier.height(16.dp).fillMaxWidth(0.3f).clip(RoundedCornerShape(4.dp)).background(brush))
        }
    }
}