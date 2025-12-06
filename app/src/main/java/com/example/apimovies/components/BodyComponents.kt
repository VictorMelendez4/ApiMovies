package com.example.apimovies.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.apimovies.model.MovieModel
import com.example.apimovies.ui.theme.AccentColor
import com.example.apimovies.ui.theme.SecondaryDark
import com.example.apimovies.ui.theme.TextGray
import com.example.apimovies.ui.theme.TextWhite

/**
 * Tarjeta principal para mostrar una película en la lista.
 * Recibe el modelo de datos y la función de navegación.
 */
@Composable
fun MovieCard(movie: MovieModel, onClick: () -> Unit) {
    // Contenedor principal de la tarjeta con bordes redondeados y sombra
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryDark),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        // Organización horizontal: Imagen a la izquierda, Información a la derecha
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Carga  de la imagen (poster) con una animación de carga
            Image(
                painter = rememberAsyncImagePainter(movie.primaryImage),
                contentDescription = null,
                contentScale = ContentScale.Crop, // Recorta la imagen para llenar el espacio
                modifier = Modifier
                    .size(width = 90.dp, height = 120.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Columna de información (Título, Año, Rating)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2, // Limita el texto a 2 líneas
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Año de lanzamiento
                Text(
                    text = movie.year?.toString() ?: "N/A",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Fila para el rating con icono de estrella
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = AccentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    // Formateo del rating a un solo decimal (ej. 8.5)
                    Text(
                        text = String.format("%.1f", movie.rating ?: 0.0),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Barra de búsqueda personalizada.
 * Incluye un botón para borrar el texto cuando hay contenido.
 */
@Composable
fun SearchBar(
    query: String,
    onSearchChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onSearchChange,
        placeholder = { Text(text = "Buscar película...", color = Color.Gray) },
        // Icono de lupa siempre visible a la izquierda
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray)
        },
        // Botón de cerrar (X) visible solo si hay texto
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                }
            }
        },
        singleLine = true,
        // Personalización de colores para eliminar la línea inferior por defecto
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SecondaryDark,
            unfocusedContainerColor = SecondaryDark,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            cursorColor = AccentColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}