package com.example.apimovies.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.apimovies.model.MovieModel
import com.example.apimovies.ui.theme.AccentColor
import com.example.apimovies.ui.theme.HighlightRed
import com.example.apimovies.ui.theme.PrimaryDark
import com.example.apimovies.ui.theme.TextGray
import com.example.apimovies.ui.theme.TextWhite
import com.example.apimovies.viewModel.MoviesViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun DetailView(
    viewModel: MoviesViewModel,
    navController: NavController,
    id: String,
    title: String?,
    photoUrl: String?,
    description: String?,
    rating: Double,
    year: Int
) {
    val context = LocalContext.current // <--- Necesario para abrir enlaces y compartir

    val cleanTitle = title?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: "Sin título"
    val cleanDesc = description?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: "Sin descripción"
    val cleanUrl = photoUrl?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) } ?: ""

    val isFavorite by viewModel.isCurrentMovieFavorite.collectAsState()

    LaunchedEffect(id) {
        viewModel.checkFavorite(id)
    }

    Scaffold(
        containerColor = PrimaryDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val movie = MovieModel(
                        id = id,
                        title = cleanTitle,
                        primaryImage = cleanUrl,
                        description = cleanDesc,
                        rating = rating,
                        year = year,
                        originalTitle = "",
                    )
                    viewModel.toggleFavorite(movie)
                },
                containerColor = HighlightRed,
                contentColor = Color.White
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito"
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {

            // IMAGEN DE FONDO
            Image(
                painter = rememberAsyncImagePainter(cleanUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            )

            // GRADIENTE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                PrimaryDark.copy(alpha = 0.8f),
                                PrimaryDark
                            ),
                            startY = 0f
                        )
                    )
            )

            // BOTÓN REGRESAR
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 40.dp, start = 16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            // CONTENIDO
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Bottom
            ) {
                Spacer(modifier = Modifier.height(450.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = cleanTitle,
                        style = MaterialTheme.typography.displaySmall,
                        color = TextWhite,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 40.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AccentColor)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "IMDb ${String.format("%.1f", rating)}",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$year",
                            color = TextGray,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "SINOPSIS",
                        style = MaterialTheme.typography.labelLarge,
                        color = TextGray,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = cleanDesc,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray,
                        textAlign = TextAlign.Start,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // --- NUEVOS BOTONES DE ACCIÓN ---
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1. BOTÓN VER EN IMDB
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/$id/"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF5C518), // Amarillo IMDb
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Ver en IMDb", fontWeight = FontWeight.Bold)
                        }

                        // 2. BOTÓN COMPARTIR
                        OutlinedButton(
                            onClick = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "¡Mira esta película: $cleanTitle! \n\nhttps://www.imdb.com/title/$id/")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Compartir película")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TextWhite)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Compartir")
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }
}