package com.ernesto.atrapasomnins.ui.ajustes

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.ernesto.atrapasomnins.data.model.CategoriaEtiqueta
import com.ernesto.atrapasomnins.notificaciones.cancelarRecordatorio
import com.ernesto.atrapasomnins.notificaciones.programarRecordatorioDiario
import com.ernesto.atrapasomnins.sensor.NivelLuz
import com.ernesto.atrapasomnins.sensor.rememberNivelLuz
import com.ernesto.atrapasomnins.ui.SuenoViewModel
import com.ernesto.atrapasomnins.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    viewModel: SuenoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val etiquetas by viewModel.etiquetas.collectAsStateWithLifecycle()
    val nivelLuz = rememberNivelLuz()

    val prefs = context.getSharedPreferences("ajustes", Context.MODE_PRIVATE)


    // Si no existe la clave, es primera instalación: forzamos todo a false
    if (!prefs.contains("primera_instalacion")) {
        prefs.edit()
            .putBoolean("recordatorio_activado", false)
            .putBoolean("primera_instalacion", true)
            .apply()
    }

    var recordatorioActivado by remember {
        mutableStateOf(prefs.getBoolean("recordatorio_activado", false))
    }
    var horaRecordatorio by remember {
        mutableStateOf(prefs.getInt("hora_recordatorio", 8))
    }
    var minutoRecordatorio by remember {
        mutableStateOf(prefs.getInt("minuto_recordatorio", 0))
    }

    var mostrarTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(
        initialHour = horaRecordatorio,
        initialMinute = minutoRecordatorio
    )

    // Comprobamos si el permiso de notificaciones ya está concedido
    val tienePermisoNotificaciones = remember {
        mutableStateOf(
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    // Launcher que recibe el resultado del diálogo de permiso del sistema
    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        tienePermisoNotificaciones.value = concedido
        if (concedido) {
            // El usuario concedió el permiso, ahora activamos el recordatorio
            recordatorioActivado = true
            prefs.edit().putBoolean("recordatorio_activado", true).apply()
            programarRecordatorioDiario(context, horaRecordatorio, minutoRecordatorio)
        } else {
            // El usuario denegó el permiso, nos aseguramos de que quede desactivado
            recordatorioActivado = false
            prefs.edit().putBoolean("recordatorio_activado", false).apply()
        }
    }

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
                                if (activado) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                        // Android 13 o superior: pedimos permiso explícito
                                        lanzadorPermiso.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        // Android 12 o inferior: no hace falta permiso explícito
                                        recordatorioActivado = true
                                        prefs.edit().putBoolean("recordatorio_activado", true).apply()
                                        programarRecordatorioDiario(context, horaRecordatorio, minutoRecordatorio)
                                    }
                                } else {
                                    recordatorioActivado = false
                                    prefs.edit().putBoolean("recordatorio_activado", false).apply()
                                    cancelarRecordatorio(context)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = LilaClaro,
                                checkedTrackColor = Morado,
                                uncheckedThumbColor = TextoApagado,
                                uncheckedTrackColor = AzulNocheMedio,
                                uncheckedBorderColor = TextoApagado.copy(alpha = 0.5f)
                            )
                        )
                    }

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
                val etiquetasPersonalizadas = etiquetas.filter { it.esPersonalizada }

                if (etiquetasPersonalizadas.isEmpty()) {
                    Text(
                        "Aún no has añadido etiquetas personalizadas.\n" +
                        "Puedes añadirlas al registrar un sueño pulsando +.",
                        color = TextoApagado,
                        fontSize = 13.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        val grupos = listOf(
                            CategoriaEtiqueta.SUENO to "Sueño",
                            CategoriaEtiqueta.COMPANERO to "Con quién dormiste",
                            CategoriaEtiqueta.LUGAR to "Dónde dormiste",
                            CategoriaEtiqueta.SUSTANCIA to "Sustancias"
                        )
                        grupos.forEach { (categoria, tituloGrupo) ->
                            val lista = etiquetasPersonalizadas.filter { it.categoria == categoria }
                            if (lista.isNotEmpty()) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        tituloGrupo.uppercase(),
                                        color = TextoApagado,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        letterSpacing = 1.sp
                                    )
                                    lista.forEach { etiqueta ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                etiqueta.nombre,
                                                color = TextoPrincipal,
                                                fontSize = 14.sp
                                            )
                                            IconButton(
                                                onClick = { viewModel.eliminarEtiqueta(etiqueta.id) },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Eliminar ${etiqueta.nombre}",
                                                    tint = RojoError.copy(alpha = 0.6f),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        HorizontalDivider(color = TextoApagado.copy(alpha = 0.1f))
                                    }
                                }
                            }
                        }
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

            // ── Sensor de luz ─────────────────────────────────
            SeccionAjustes(titulo = "Sensor de luz") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Nivel de luz en la habitación ahora mismo:",
                        color = TextoApagado,
                        fontSize = 13.sp
                    )
                    val (emoji, descripcion, colorLuz) = when (nivelLuz) {
                        NivelLuz.OSCURO -> Triple("🌑", "Oscuro, buena luz para dormir", TextoApagado)
                        NivelLuz.NORMAL -> Triple("🌤️", "Luz normal", LilaClaro)
                        NivelLuz.BRILLANTE -> Triple("☀️", "Muy iluminado", AmarilloAviso)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(emoji, fontSize = 36.sp)
                        Column {
                            Text(descripcion, color = colorLuz, fontWeight = FontWeight.Medium)
                            Text(
                                when (nivelLuz) {
                                    NivelLuz.OSCURO -> "Menos de 10 lux"
                                    NivelLuz.NORMAL -> "Entre 10 y 1000 lux"
                                    NivelLuz.BRILLANTE -> "Más de 1000 lux"
                                },
                                color = TextoApagado,
                                fontSize = 12.sp
                            )
                        }
                    }
                    val progreso = when (nivelLuz) {
                        NivelLuz.OSCURO -> 0.1f
                        NivelLuz.NORMAL -> 0.5f
                        NivelLuz.BRILLANTE -> 1.0f
                    }
                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = colorLuz,
                        trackColor = AzulNoche
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
