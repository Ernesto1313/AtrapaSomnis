package com.ernesto.atrapasomnins.ui.registro.pasos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ernesto.atrapasomnins.ui.registro.RegistroViewModel
import com.ernesto.atrapasomnins.ui.theme.*

@Composable
fun PasoContextoNoche(viewModel: RegistroViewModel) {
    val coordenadas by viewModel.coordenadas.collectAsStateWithLifecycle()
    val ubicaciones by viewModel.ubicaciones.collectAsStateWithLifecycle()
    val ubicacionSeleccionadaId by viewModel.ubicacionSeleccionadaId.collectAsStateWithLifecycle()
    val cansancio by viewModel.cansancio.collectAsStateWithLifecycle()
    val estres by viewModel.estres.collectAsStateWithLifecycle()
    val companero by viewModel.companero.collectAsStateWithLifecycle()
    val comidaPesada by viewModel.comidaPesada.collectAsStateWithLifecycle()
    val alcohol by viewModel.alcohol.collectAsStateWithLifecycle()
    val otrasSustancias by viewModel.otrasSustancias.collectAsStateWithLifecycle()
    val melatonina by viewModel.melatonina.collectAsStateWithLifecycle()
    val pantallas by viewModel.pantallas.collectAsStateWithLifecycle()
    val lectura by viewModel.lectura.collectAsStateWithLifecycle()
    val ejercicio by viewModel.ejercicio.collectAsStateWithLifecycle()
    val temperatura by viewModel.temperatura.collectAsStateWithLifecycle()

    // Estado local para el diálogo de nueva ubicación
    var mostrarDialogoUbicacion by remember { mutableStateOf(false) }
    var nombreNuevaUbicacion by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            text = "La noche anterior",
            color = TextoPrincipal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Estos datos ayudan a entender tus sueños con el tiempo",
            color = TextoApagado,
            fontSize = 14.sp
        )

        // ── Compañero de cama ─────────────────────────────────
        SeccionContexto(titulo = "¿Con quién dormiste?") {
            val opciones = listOf("solo" to "Solo", "pareja" to "Pareja",
                                  "amigo" to "Amigo/a", "otro" to "Otro")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                opciones.forEach { (id, texto) ->
                    FilterChip(
                        selected = companero == id,
                        onClick = {
                            viewModel.companero.value = if (companero == id) null else id
                        },
                        label = { Text(texto) },
                        colors = chipColors(companero == id),
                        border = chipBorder(companero == id)
                    )
                }
            }
        }

        // ── Cansancio ────────────────────────────────────────
        SeccionContexto(titulo = "Cansancio al acostarte") {
            SelectorEstrellas(
                valor = cansancio,
                onCambio = { viewModel.cansancio.value = it }
            )
        }

        // ── Estrés ───────────────────────────────────────────
        SeccionContexto(titulo = "Estrés del día") {
            SelectorEstrellas(
                valor = estres,
                onCambio = { viewModel.estres.value = it }
            )
        }

        // ── Hábitos ──────────────────────────────────────────
        SeccionContexto(titulo = "Hábitos de ayer") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaSiNo("¿Comida pesada?", comidaPesada) {
                    viewModel.comidaPesada.value = it
                }
                FilaSiNo("¿Pantallas antes de dormir?", pantallas) {
                    viewModel.pantallas.value = it
                }
                FilaSiNo("¿Lectura antes de dormir?", lectura) {
                    viewModel.lectura.value = it
                }
                FilaSiNo("¿Ejercicio ese día?", ejercicio) {
                    viewModel.ejercicio.value = it
                }
            }
        }

        // ── Sustancias ───────────────────────────────────────
        SeccionContexto(titulo = "Sustancias") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaSiNo("¿Alcohol?", alcohol) { viewModel.alcohol.value = it }
                FilaSiNo("¿Otras sustancias?", otrasSustancias) {
                    viewModel.otrasSustancias.value = it
                }
                FilaSiNo("¿Melatonina?", melatonina) { viewModel.melatonina.value = it }
            }
        }

        // ── Temperatura ──────────────────────────────────────
        SeccionContexto(titulo = "Temperatura en la habitación") {
            val opciones = listOf("frio" to "🥶 Frío", "normal" to "😊 Normal",
                                  "calor" to "🥵 Calor")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                opciones.forEach { (id, texto) ->
                    FilterChip(
                        selected = temperatura == id,
                        onClick = {
                            viewModel.temperatura.value = if (temperatura == id) null else id
                        },
                        label = { Text(texto) },
                        colors = chipColors(temperatura == id),
                        border = chipBorder(temperatura == id)
                    )
                }
            }
        }

        // ── Ubicación ────────────────────────────────────────
        SeccionContexto(titulo = "¿Dónde dormiste?") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (ubicaciones.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ubicaciones.forEach { ubi ->
                            FilterChip(
                                selected = ubicacionSeleccionadaId == ubi.id,
                                onClick = {
                                    viewModel.ubicacionSeleccionadaId.value =
                                        if (ubicacionSeleccionadaId == ubi.id) null else ubi.id
                                },
                                label = { Text(ubi.nombre) },
                                colors = chipColors(ubicacionSeleccionadaId == ubi.id),
                                border = chipBorder(ubicacionSeleccionadaId == ubi.id)
                            )
                        }
                    }
                }

                // Botón para guardar la ubicación actual con un nombre
                OutlinedButton(
                    onClick = { mostrarDialogoUbicacion = true },
                    enabled = coordenadas != null,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LilaClaro),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, LilaClaro.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (coordenadas != null) "Guardar ubicación actual"
                        else "GPS no disponible"
                    )
                }
            }
        }

        // Botón final para guardar el sueño completo
        BotonSiguiente(
            onClick = { viewModel.avanzar() },
            texto = "Guardar sueño ✓"
        )
        Spacer(Modifier.height(16.dp))
    }

    // Diálogo para nombrar la nueva ubicación
    if (mostrarDialogoUbicacion) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoUbicacion = false
                nombreNuevaUbicacion = ""
            },
            containerColor = AzulNocheMedio,
            title = {
                Text("Nombrar ubicación", color = TextoPrincipal, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "¿Cómo quieres llamar a este lugar?",
                        color = TextoApagado,
                        fontSize = 13.sp
                    )
                    OutlinedTextField(
                        value = nombreNuevaUbicacion,
                        onValueChange = { nombreNuevaUbicacion = it },
                        placeholder = {
                            Text(
                                "Mi casa, Casa Berfin, WG Aachen...",
                                color = TextoApagado.copy(alpha = 0.5f)
                            )
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Morado,
                            unfocusedBorderColor = TextoApagado.copy(alpha = 0.3f),
                            focusedTextColor = TextoPrincipal,
                            unfocusedTextColor = TextoPrincipal,
                            cursorColor = LilaClaro,
                            focusedContainerColor = AzulNocheMedio,
                            unfocusedContainerColor = AzulNocheMedio
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nombreNuevaUbicacion.isNotBlank()) {
                            viewModel.guardarNuevaUbicacion(nombreNuevaUbicacion.trim())
                            nombreNuevaUbicacion = ""
                            mostrarDialogoUbicacion = false
                        }
                    }
                ) {
                    Text("Guardar", color = LilaClaro)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoUbicacion = false
                    nombreNuevaUbicacion = ""
                }) {
                    Text("Cancelar", color = TextoApagado)
                }
            }
        )
    }
}

// ── Componentes auxiliares del contexto ─────────────────────

// Sección con título y contenido
@Composable
private fun SeccionContexto(
    titulo: String,
    contenido: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, color = TextoPrincipal, fontWeight = FontWeight.Medium, fontSize = 15.sp)
        contenido()
        HorizontalDivider(color = TextoApagado.copy(alpha = 0.15f))
    }
}

// Fila de Sí/No/Sin respuesta para hábitos
@Composable
private fun FilaSiNo(
    texto: String,
    valor: Boolean?,
    onChange: (Boolean?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(texto, color = TextoApagado, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            // Botón Sí
            FilaBotonOpcion("Sí", valor == true, { onChange(if (valor == true) null else true) })
            // Botón No
            FilaBotonOpcion("No", valor == false, { onChange(if (valor == false) null else false) })
        }
    }
}

@Composable
private fun FilaBotonOpcion(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        modifier = Modifier.height(32.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (seleccionado) Morado.copy(alpha = 0.3f)
                             else androidx.compose.ui.graphics.Color.Transparent,
            contentColor = if (seleccionado) LilaClaro else TextoApagado
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (seleccionado) Morado else TextoApagado.copy(alpha = 0.3f)
        )
    ) {
        Text(texto, fontSize = 13.sp)
    }
}

// Selector de 1 a 5 con estrellas
@Composable
private fun SelectorEstrellas(valor: Int, onCambio: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        (1..5).forEach { nivel ->
            IconButton(
                onClick = { onCambio(nivel) },
                modifier = Modifier.size(36.dp)
            ) {
                Text(
                    text = "★",
                    fontSize = 24.sp,
                    color = if (nivel <= valor) LilaClaro
                            else TextoApagado.copy(alpha = 0.3f)
                )
            }
        }
    }
}

// Colores reutilizables para los chips de este paso
@Composable
private fun chipColors(seleccionado: Boolean) = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Morado,
    selectedLabelColor = TextoPrincipal,
    containerColor = AzulNocheMedio,
    labelColor = TextoApagado
)

@Composable
private fun chipBorder(seleccionado: Boolean) = FilterChipDefaults.filterChipBorder(
    enabled = true,
    selected = seleccionado,
    borderColor = TextoApagado.copy(alpha = 0.3f),
    selectedBorderColor = Morado
)
