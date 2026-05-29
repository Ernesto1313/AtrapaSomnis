package com.ernesto.atrapasomnins.ui.inicio

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.NightShelter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ernesto.atrapasomnins.data.model.EstadoSueno
import com.ernesto.atrapasomnins.data.model.Sueno
import com.ernesto.atrapasomnins.ui.SuenoViewModel
import com.ernesto.atrapasomnins.ui.theme.*
import com.ernesto.atrapasomnins.sensor.NivelLuz
import com.ernesto.atrapasomnins.sensor.rememberNivelLuz
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    onNuevoSueno: () -> Unit,
    onVerDetalle: (String) -> Unit,
    viewModel: SuenoViewModel = hiltViewModel()
) {
    val nivelLuz = rememberNivelLuz()

    // El fondo cambia ligeramente según la luz ambiente
    val colorFondo = when (nivelLuz) {
        NivelLuz.OSCURO -> AzulNoche                       // muy oscuro
        NivelLuz.NORMAL -> AzulNoche                       // normal
        NivelLuz.BRILLANTE -> AzulNoche.copy(alpha = 0.9f) // un poco más claro
    }

    val suenos by viewModel.suenos.collectAsStateWithLifecycle()
    val etiquetas by viewModel.etiquetas.collectAsStateWithLifecycle()
    val filtroActivo by viewModel.filtroEtiqueta.collectAsStateWithLifecycle()

    // Lista que se muestra, ya filtrada
    val suenosMostrados = remember(suenos, filtroActivo) {
        viewModel.suenosFiltrados()
    }

    Scaffold(
        containerColor = colorFondo,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = LilaClaro,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "AtrapaSomnis",
                            color = LilaClaro,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AzulNocheMedio
                )
            )
        },
        floatingActionButton = {
            // Botón para registrar un sueño nuevo
            FloatingActionButton(
                onClick = onNuevoSueno,
                containerColor = Morado,
                contentColor = TextoPrincipal,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Registrar sueño")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(colorFondo)
        ) {
            // Fila de filtros por etiqueta
            if (etiquetas.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Chip para quitar el filtro y ver todos
                    item {
                        FilterChip(
                            selected = filtroActivo == null,
                            onClick = { viewModel.cambiarFiltro(null) },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Morado,
                                selectedLabelColor = TextoPrincipal,
                                containerColor = Color.Transparent,
                                labelColor = TextoApagado
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = filtroActivo == null,
                                borderColor = TextoApagado,
                                selectedBorderColor = Color.Transparent
                            )
                        )
                    }
                    // Un chip por cada etiqueta disponible
                    items(etiquetas) { etiqueta ->
                        FilterChip(
                            selected = filtroActivo == etiqueta.id,
                            onClick = {
                                viewModel.cambiarFiltro(
                                    if (filtroActivo == etiqueta.id) null else etiqueta.id
                                )
                            },
                            label = { Text(etiqueta.nombre) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Morado,
                                selectedLabelColor = TextoPrincipal,
                                containerColor = Color.Transparent,
                                labelColor = TextoApagado
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = filtroActivo == etiqueta.id,
                                borderColor = TextoApagado,
                                selectedBorderColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            // Lista de sueños o mensaje vacío
            if (suenosMostrados.isEmpty()) {
                // Pantalla vacía con mensaje motivador
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.NightShelter,
                            contentDescription = null,
                            tint = TextoApagado,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(
                            text = "Aún no hay sueños registrados",
                            color = TextoApagado,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Pulsa + para añadir tu primer sueño",
                            color = TextoApagado.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(suenosMostrados, key = { it.id }) { sueno ->
                        TarjetaSueno(
                            sueno = sueno,
                            etiquetasDisponibles = etiquetas,
                            onClick = { onVerDetalle(sueno.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaSueno(
    sueno: Sueno,
    etiquetasDisponibles: List<com.ernesto.atrapasomnins.data.model.Etiqueta>,
    onClick: () -> Unit
) {
    // Formateamos la fecha para mostrarla de forma legible
    val formato = SimpleDateFormat("EEE d MMM · HH:mm", Locale("es"))
    val fechaTexto = formato.format(Date(sueno.fechaCreacion))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AzulNocheMedio),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Cabecera: fecha y estado del sueño
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fechaTexto,
                    color = TextoApagado,
                    fontSize = 12.sp
                )
                // Indicador visual del estado
                EstadoBadge(estado = sueno.estado)
            }

            when (sueno.estado) {
                EstadoSueno.RECORDADO -> {
                    // Título del sueño
                    Text(
                        text = sueno.titulo ?: "Sin título",
                        color = TextoPrincipal,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    // Primeras palabras de la descripción como preview
                    if (!sueno.descripcion.isNullOrBlank()) {
                        Text(
                            text = sueno.descripcion,
                            color = TextoApagado,
                            fontSize = 13.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    // Etiquetas del sueño
                    if (sueno.etiquetas.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(sueno.etiquetas) { idEtiqueta ->
                                val etiqueta = etiquetasDisponibles
                                    .find { it.id == idEtiqueta }
                                if (etiqueta != null) {
                                    EtiquetaChip(nombre = etiqueta.nombre)
                                }
                            }
                        }
                    }
                }
                EstadoSueno.NO_RECUERDO -> {
                    Text(
                        text = "Soñé, pero no recuerdo nada",
                        color = TextoApagado,
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
                EstadoSueno.NO_HE_SONADO -> {
                    Text(
                        text = "No he soñado esta noche",
                        color = TextoApagado,
                        fontSize = 14.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Intensidad si está disponible
            if (sueno.intensidad != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    repeat(sueno.intensidad) {
                        Text("★", color = LilaClaro, fontSize = 12.sp)
                    }
                    repeat(5 - sueno.intensidad) {
                        Text("★", color = TextoApagado.copy(alpha = 0.3f), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoBadge(estado: EstadoSueno) {
    val (texto, color) = when (estado) {
        EstadoSueno.RECORDADO -> "Recordado" to LilaClaro
        EstadoSueno.NO_RECUERDO -> "Difuso" to AmarilloAviso
        EstadoSueno.NO_HE_SONADO -> "Sin sueños" to TextoApagado
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = texto, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun EtiquetaChip(nombre: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Morado.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = nombre, color = LilaClaro, fontSize = 11.sp)
    }
}
