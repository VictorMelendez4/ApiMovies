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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import com.example.apimovies.ui.theme.SecondaryDark
import com.example.apimovies.ui.theme.TextWhite
import com.example.apimovies.ui.theme.AccentColor

@Composable
fun MovieCard(movie: MovieModel, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SecondaryDark),
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)) // Sombra
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster
            Image(
                painter = rememberAsyncImagePainter(movie.primaryImage),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 90.dp, height = 120.dp) // Formato vertical de poster
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Gray) // Fondo mientras carga
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Año
                Text(
                    text = movie.year?.toString() ?: "N/A",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Rating con Estrella
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = AccentColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${movie.rating ?: "0.0"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AccentColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
@Composable
fun SearchBar(
    query: String,
    onSearchChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onSearchChange,
        placeholder = { Text(text = "Buscar película...", color = Color.Gray) },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onSearchChange("") }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.Gray)
                }
            }
        },
        singleLine = true,
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