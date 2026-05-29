package com.ernesto.atrapasomnins.ui.ajustes

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ernesto.atrapasomnins.notificaciones.cancelarRecordatorio
import com.ernesto.atrapasomnins.notificaciones.programarRecordatorioDiario
import com.ernesto.atrapasomnins.ui.SuenoViewModel
import com.ernesto.atrapasomnins.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    viewModel: SuenoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val etiquetas by viewModel.etiquetas.collectAsStateWithLifecycle()

    // Preferencias del recordatorio guardadas en SharedPreferences
    val prefs = context.getSharedPreferences("ajustes", Context.MODE_PRIVATE)
    var recordatorioActivado by remember {
        mutableStateOf(prefs.getBoolean("recordatorio_activado", false))
    }
    var horaRecordatorio by remember {
        mutableStateOf(prefs.getInt("hora_recordatorio", 8))
    }
    var minutoRecordatorio by remember {
        mutableStateOf(prefs.getInt("minuto_recordatorio", 0))
    }

    // Control del diálogo de nueva etiqueta
    var mostrarDialogoEtiqueta by remember { mutableStateOf(false) }
    var textoNuevaEtiqueta by remember { mutableStateOf("") }

    // Control del TimePicker
    var mostrarTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = horaRecordatorio,
        initialMinute = minutoRecordatorio
    )

    Scaffold(
        containerColor = AzulNoche,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Ajustes", color = LilaClaro, fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AzulNocheMedio
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ── Recordatorio matutino ─────────────────────────
            SeccionAjustes(titulo = "Recordatorio matutino") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Recordatorio diario",
                                color = TextoPrincipal,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Te avisamos para que registres tu sueño",
                                color = TextoApagado,
                                fontSize = 12.sp
                            )
                        }
                        Switch(
                            checked = recordatorioActivado,
                            onCheckedChange = { activado ->
                                recordatorioActivado = activado
                                prefs.edit()
                                    .putBoolean("recordatorio_activado", activado)
                                    .apply()
                                if (activado) {
                                    programarRecordatorioDiario(
                                        context, horaRecordatorio, minutoRecordatorio
                                    )
                                } else {
                                    cancelarRecordatorio(context)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LilaClaro,
                                checkedTrackColor = Morado
                            )
                        )
                    }

                    // Selector de hora (solo si el recordatorio está activo)
                    if (recordatorioActivado) {
                        OutlinedButton(
                            onClick = { mostrarTimePicker = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = LilaClaro
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, LilaClaro.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Hora: %02d:%02d".format(horaRecordatorio, minutoRecordatorio),
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }

            // ── Gestión de etiquetas ──────────────────────────
            SeccionAjustes(titulo = "Mis etiquetas") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Solo mostramos las personalizadas, las predefinidas no se borran
                    val personalizadas = etiquetas.filter { it.esPersonalizada }
                    if (personalizadas.isEmpty()) {
                        Text(
                            "Aún no has creado etiquetas personalizadas",
                            color = TextoApagado,
                            fontSize = 13.sp
                        )
                    } else {
                        personalizadas.forEach { etiqueta ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(etiqueta.nombre, color = TextoPrincipal)
                                IconButton(
                                    onClick = {
                                        // TODO: añadir eliminar etiqueta al repositorio
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar",
                                        tint = RojoError.copy(alpha = 0.6f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            HorizontalDivider(color = TextoApagado.copy(alpha = 0.1f))
                        }
                    }

                    // Botón para añadir nueva etiqueta
                    OutlinedButton(
                        onClick = { mostrarDialogoEtiqueta = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LilaClaro),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, LilaClaro.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Nueva etiqueta")
                    }
                }
            }

            // ── Información de la app ─────────────────────────
            SeccionAjustes(titulo = "Acerca de") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("AtrapaSomnis", color = TextoPrincipal, fontWeight = FontWeight.Medium)
                    Text("Versión 1.0", color = TextoApagado, fontSize = 13.sp)
                    Text(
                        "Tu diario personal de sueños",
                        color = TextoApagado,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

    // TimePicker para elegir la hora del recordatorio
    if (mostrarTimePicker) {
        AlertDialog(
            onDismissRequest = { mostrarTimePicker = false },
            containerColor = AzulNocheMedio,
            title = {
                Text("Hora del recordatorio", color = TextoPrincipal, fontWeight = FontWeight.Bold)
            },
            text = {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = AzulNoche,
                        selectorColor = Morado,
                        containerColor = AzulNocheMedio,
                        periodSelectorSelectedContainerColor = Morado,
                        timeSelectorSelectedContainerColor = Morado
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    horaRecordatorio = timePickerState.hour
                    minutoRecordatorio = timePickerState.minute
                    prefs.edit()
                        .putInt("hora_recordatorio", horaRecordatorio)
                        .putInt("minuto_recordatorio", minutoRecordatorio)
                        .apply()
                    if (recordatorioActivado) {
                        programarRecordatorioDiario(context, horaRecordatorio, minutoRecordatorio)
                    }
                    mostrarTimePicker = false
                }) {
                    Text("Guardar", color = LilaClaro)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarTimePicker = false }) {
                    Text("Cancelar", color = TextoApagado)
                }
            }
        )
    }

    // Diálogo para crear nueva etiqueta
    if (mostrarDialogoEtiqueta) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoEtiqueta = false
                textoNuevaEtiqueta = ""
            },
            containerColor = AzulNocheMedio,
            title = {
                Text("Nueva etiqueta", color = TextoPrincipal, fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = textoNuevaEtiqueta,
                    onValueChange = { textoNuevaEtiqueta = it },
                    placeholder = {
                        Text("Nombre", color = TextoApagado.copy(alpha = 0.5f))
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
            },
            confirmButton = {
                TextButton(onClick = {
                    if (textoNuevaEtiqueta.isNotBlank()) {
                        viewModel.crearEtiqueta(textoNuevaEtiqueta.trim())
                        textoNuevaEtiqueta = ""
                        mostrarDialogoEtiqueta = false
                    }
                }) {
                    Text("Crear", color = LilaClaro)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoEtiqueta = false
                    textoNuevaEtiqueta = ""
                }) {
                    Text("Cancelar", color = TextoApagado)
                }
            }
        )
    }
}

@Composable
private fun SeccionAjustes(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            titulo,
            color = LilaClaro,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AzulNocheMedio)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                contenido()
            }
        }
    }
}
