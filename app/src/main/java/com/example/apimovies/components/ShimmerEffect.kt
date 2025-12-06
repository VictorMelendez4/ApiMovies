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

/**
 * Componente de carga "Skeleton" (Shimmer).
 * Muestra una animación de brillo sobre tarjetas falsas para indicar que
 * los datos se están cargando, mejorando la percepción de velocidad de la app.
 */
@Composable
fun ShimmerLoadingAnimation() {
    // 1. Configuración del Gradiente Animado
    // Definimos una lista de colores que va de oscuro a claro y vuelve a oscuro
    // para simular un reflejo de luz pasando.
    val shimmerColors = listOf(
        SecondaryDark.copy(alpha = 0.6f),
        SecondaryDark.copy(alpha = 0.2f), // El centro es más claro (el brillo)
        SecondaryDark.copy(alpha = 0.6f),
    )

    // 2. Motor de Animación
    // Configuramos una transición infinita que mueve un valor float de 0 a 1000
    // repetidamente cada 1 segundo (1000ms).
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

    // 3. Creación del Pincel (Brush)
    // El pincel usa el valor animado para desplazar el gradiente diagonalmente,
    // creando el efecto visual de movimiento.
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    // 4. Renderizado de la Lista Falsa
    // Dibujamos 6 tarjetas vacías usando el pincel animado como fondo.
    LazyColumn(contentPadding = PaddingValues(10.dp)) {
        items(6) {
            ShimmerMovieCardItem(brush = brush)
        }
    }
}

/**
 * Tarjeta individual del esqueleto.
 * Imita la estructura geométrica de `MovieCard`
 * pero usando cajas grises (Spacers) en lugar de contenido real.
 */
@Composable
fun ShimmerMovieCardItem(brush: Brush) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Placeholder para la Imagen (Poster)
        Spacer(
            modifier = Modifier
                .size(width = 90.dp, height = 120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(brush) // Aplicamos el fondo animado
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Placeholder para los Textos (Título, Año, Rating)
        Column(modifier = Modifier.weight(1f)) {
            // Línea larga simulando el Título
            Spacer(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Línea corta simulando el Año/Rating
            Spacer(
                modifier = Modifier
                    .height(16.dp)
                    .fillMaxWidth(0.3f)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}