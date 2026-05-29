package com.ernesto.atrapasomnins.ui.detalle

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ernesto.atrapasomnins.data.model.ContextoNoche
import com.ernesto.atrapasomnins.data.model.EstadoSueno
import com.ernesto.atrapasomnins.data.model.Sueno
import com.ernesto.atrapasomnins.ui.SuenoViewModel
import com.ernesto.atrapasomnins.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSuenoScreen(
    id: String,
    onVolver: () -> Unit,
    viewModel: SuenoViewModel = hiltViewModel()
) {
    val suenos by viewModel.suenos.collectAsStateWithLifecycle()
    val etiquetas by viewModel.etiquetas.collectAsStateWithLifecycle()
    val sueno = suenos.find { it.id == id }

    LaunchedEffect(id) { viewModel.cargarDatos() }

    var mostrarConfirmacionBorrar by remember { mutableStateOf(false) }

    if (suenos.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Morado)
        }
        return
    }

    if (sueno == null) {
        LaunchedEffect(Unit) { onVolver() }
        return
    }

    val formato = SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy · HH:mm", Locale("es"))
    val fechaTexto = formato.format(Date(sueno.fechaCreacion))

    Scaffold(
        containerColor = AzulNoche,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = sueno.titulo ?: "Sueño sin título",
                        color = TextoPrincipal,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = LilaClaro
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarConfirmacionBorrar = true }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = RojoError.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AzulNocheMedio
                )
            )
        }
    ) { padding ->
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { visible = true }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(400))
        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(fechaTexto, color = TextoApagado, fontSize = 12.sp)
                EstadoBadgeDetalle(estado = sueno.estado)
            }

            when (sueno.estado) {
                EstadoSueno.RECORDADO -> ContenidoRecordado(sueno, etiquetas)
                EstadoSueno.NO_RECUERDO -> {
                    TarjetaInfo("🌫️ Soñaste pero no recuerdas nada de este sueño.")
                }
                EstadoSueno.NO_HE_SONADO -> {
                    TarjetaInfo("😴 No soñaste esta noche.")
                }
            }

            if (sueno.causasDespertar.isNotEmpty()) {
                SeccionDetalle(titulo = "Te despertó") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        sueno.causasDespertar.forEach { causa ->
                            ChipDetalle(texto = textoDespertar(causa))
                        }
                    }
                }
            }

            sueno.contextoNoche?.let { contexto ->
                SeccionDetalle(titulo = "Contexto de la noche") {
                    ContextoDetalle(contexto = contexto, todasEtiquetas = etiquetas)
                }
            }
        }
        } // AnimatedVisibility
    }

    if (mostrarConfirmacionBorrar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionBorrar = false },
            containerColor = AzulNocheMedio,
            title = {
                Text("¿Eliminar este sueño?", color = TextoPrincipal, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Esta acción no se puede deshacer.",
                    color = TextoApagado
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarSueno(sueno.id)
                    onVolver()
                }) {
                    Text("Eliminar", color = RojoError)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionBorrar = false }) {
                    Text("Cancelar", color = TextoApagado)
                }
            }
        )
    }
}

@Composable
private fun ContenidoRecordado(
    sueno: Sueno,
    etiquetas: List<com.ernesto.atrapasomnins.data.model.Etiqueta>
) {
    if (!sueno.descripcion.isNullOrBlank()) {
        SeccionDetalle(titulo = "Descripción") {
            Text(
                text = sueno.descripcion,
                color = TextoPrincipal,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        if (sueno.intensidad != null) {
            TarjetaDato(
                etiqueta = "Intensidad",
                valor = "★".repeat(sueno.intensidad) +
                        "☆".repeat(5 - sueno.intensidad),
                modifier = Modifier.weight(1f)
            )
        }
        if (sueno.lucido != null) {
            TarjetaDato(
                etiqueta = "Lúcido",
                valor = if (sueno.lucido) "Sí ✨" else "No",
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (sueno.etiquetas.isNotEmpty()) {
        SeccionDetalle(titulo = "Etiquetas") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                sueno.etiquetas.forEach { idEtiqueta ->
                    val etiqueta = etiquetas.find { it.id == idEtiqueta }
                    if (etiqueta != null) {
                        ChipDetalle(texto = etiqueta.nombre)
                    }
                }
            }
        }
    }
}

@Composable
private fun ContextoDetalle(
    contexto: ContextoNoche,
    todasEtiquetas: List<com.ernesto.atrapasomnins.data.model.Etiqueta>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (contexto.etiquetasCompanero.isNotEmpty()) {
            val nombres = contexto.etiquetasCompanero.mapNotNull { id ->
                todasEtiquetas.find { it.id == id }?.nombre
            }.joinToString(", ")
            FilaDato("Con quién", nombres)
        }
        if (contexto.etiquetasLugar.isNotEmpty()) {
            val nombres = contexto.etiquetasLugar.mapNotNull { id ->
                todasEtiquetas.find { it.id == id }?.nombre
            }.joinToString(", ")
            FilaDato("Dónde", nombres)
        }
        contexto.cansancio?.let {
            FilaDato("Cansancio", "★".repeat(it) + "☆".repeat(5 - it))
        }
        contexto.estres?.let {
            FilaDato("Estrés", "★".repeat(it) + "☆".repeat(5 - it))
        }
        contexto.comidaPesada?.let {
            FilaDato("Comida pesada", if (it) "Sí" else "No")
        }
        contexto.pantallas?.let {
            FilaDato("Pantallas", if (it) "Sí" else "No")
        }
        contexto.lectura?.let {
            FilaDato("Lectura", if (it) "Sí" else "No")
        }
        contexto.ejercicio?.let {
            FilaDato("Ejercicio", if (it) "Sí" else "No")
        }
        if (contexto.etiquetasSustancia.isNotEmpty()) {
            val nombres = contexto.etiquetasSustancia.mapNotNull { id ->
                todasEtiquetas.find { it.id == id }?.nombre
            }.joinToString(", ")
            FilaDato("Sustancias", nombres)
        }
        contexto.temperatura?.let {
            val texto = when (it) {
                "frio" -> "🥶 Frío"
                "fresco" -> "❄️ Fresco"
                "calido" -> "🌤️ Cálido"
                "calor" -> "🥵 Calor"
                else -> "😊 Normal"
            }
            FilaDato("Temperatura", texto)
        }
    }
}

// ── Componentes auxiliares ───────────────────────────────────

@Composable
private fun SeccionDetalle(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, color = LilaClaro, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AzulNocheMedio)
        ) {
            Box(modifier = Modifier.padding(12.dp)) {
                contenido()
            }
        }
    }
}

@Composable
private fun TarjetaDato(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AzulNocheMedio),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(etiqueta, color = TextoApagado, fontSize = 11.sp)
            Text(valor, color = TextoPrincipal, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun FilaDato(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(etiqueta, color = TextoApagado, fontSize = 13.sp)
        Text(valor, color = TextoPrincipal, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun ChipDetalle(texto: String) {
    Box(
        modifier = Modifier
            .background(Morado.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(texto, color = LilaClaro, fontSize = 12.sp)
    }
}

@Composable
private fun TarjetaInfo(texto: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AzulNocheMedio)
    ) {
        Text(
            text = texto,
            color = TextoApagado,
            fontSize = 15.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun EstadoBadgeDetalle(estado: EstadoSueno) {
    val (texto, color) = when (estado) {
        EstadoSueno.RECORDADO -> "Recordado" to LilaClaro
        EstadoSueno.NO_RECUERDO -> "Sin recuerdo" to AmarilloAviso
        EstadoSueno.NO_HE_SONADO -> "Sin sueño" to TextoApagado
    }
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(texto, color = color, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

private fun textoDespertar(id: String): String = when (id) {
    "alarma" -> "⏰ Alarma"
    "luz" -> "☀️ Luz"
    "ruido" -> "🔊 Ruido"
    "sueno_propio" -> "💭 El propio sueño"
    "alguien" -> "🙋 Alguien"
    "nada" -> "✨ Nada"
    "llamada" -> "📞 Llamada"
    "timbre" -> "🔔 Timbre"
    "sed_calor_frio" -> "🌡️ Sed/calor/frío"
    "solo" -> "🌅 Solo me desperté"
    else -> id
}
