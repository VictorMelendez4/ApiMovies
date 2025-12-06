package com.example.apimovies.views

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.example.apimovies.ui.theme.*
import com.example.apimovies.viewModel.MoviesViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Vista de Detalle.
 *
 * Muestra la información completa de una película seleccionada.
 */
@Composable
fun DetailView(
    viewModel: MoviesViewModel,
    navController: NavController,
    id: String
) {
    // Contexto de Android: Necesario para lanzar Intents (Navegador Web y Compartir)
    val context = LocalContext.current



    // Carga de Datos: Al entrar a la pantalla, solicitamos la info detallada por ID.
    // 'LaunchedEffect' asegura que esto solo pase una vez al iniciar.
    LaunchedEffect(id) {
        viewModel.getMovieById(id)
    }

    // Observadores: Escuchamos los cambios en la película seleccionada y si es favorita.
    val movie by viewModel.selectedMovie.collectAsState()
    val isFavorite by viewModel.isCurrentMovieFavorite.collectAsState()

    Scaffold(
        containerColor = PrimaryDark,
        // Botón Flotante para Agregar/Quitar de Favoritos
        floatingActionButton = {
            // Solo mostramos el botón si los datos de la película ya cargaron
            movie?.let { safeMovie ->
                FloatingActionButton(
                    onClick = { viewModel.toggleFavorite(safeMovie) },
                    containerColor = HighlightRed,
                    contentColor = Color.White
                ) {
                    // Cambia el icono según el estado
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorito"
                    )
                }
            }
        }
    ) { paddingValues ->
        // Contenedor base para superponer elementos
        Box(modifier = Modifier.fillMaxSize()) {

            // Si la película es nula (aún cargando), no mostramos nada o podríamos poner un loader.
            movie?.let { item ->

                //  IMAGEN DE FONDO
                Image(
                    painter = rememberAsyncImagePainter(item.primaryImage),
                    contentDescription = null,
                    contentScale = ContentScale.Crop, // Recorta para llenar el ancho
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(600.dp) // Ocupa gran parte de la pantalla
                )

                // Oscurece la imagen gradualmente para que el texto blanco sea legible
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

                // CONTENIDO SCROLLEABLE
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState()), // Permite bajar si el texto es largo
                    verticalArrangement = Arrangement.Bottom // Empuja todo hacia abajo
                ) {
                    // Espaciador invisible para dejar ver la imagen limpia arriba
                    Spacer(modifier = Modifier.height(450.dp))

                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        // Título Grande
                        Text(
                            text = item.title ?: "Sin Título",
                            style = MaterialTheme.typography.displaySmall,
                            color = TextWhite,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 40.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Chips de Información (Rating y Año)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Chip de Calificación
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentColor)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "IMDb ${String.format("%.1f", item.rating ?: 0.0)}",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Chip de Año
                            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${item.year ?: 0}",
                                color = TextGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sección Sinopsis
                        Text(
                            text = "SINOPSIS",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextGray,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.description ?: "Sin descripción disponible",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextGray,
                            textAlign = TextAlign.Start,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // BOTONES DE ACCIÓN EXTERNA
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Abrir enlace en navegador
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.imdb.com/title/$id/"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF5C518),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Info, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "IMDb", fontWeight = FontWeight.Bold)
                            }

                            // Compartir texto plano
                            OutlinedButton(
                                onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "¡Checa esta peli: ${item.title}! \n\nhttps://www.imdb.com/title/$id/")
                                        type = "text/plain"
                                    }
                                    val shareIntent = Intent.createChooser(sendIntent, "Compartir")
                                    context.startActivity(shareIntent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                                border = androidx.compose.foundation.BorderStroke(1.dp, TextWhite)
                            ) {
                                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Share")
                            }
                        }

                        // Espacio final para que el FAB no tape el contenido
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }

            // Botón de regresar (Siempre visible y encima de todo)
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .padding(top = 40.dp, start = 16.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        }
    }
}