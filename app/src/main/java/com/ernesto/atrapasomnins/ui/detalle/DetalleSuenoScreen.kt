package com.ernesto.atrapasomnins.ui.detalle

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
    // Buscamos el sueño concreto en la lista ya cargada
    val suenos by viewModel.suenos.collectAsStateWithLifecycle()
    val etiquetas by viewModel.etiquetas.collectAsStateWithLifecycle()
    val ubicaciones by viewModel.ubicaciones.collectAsStateWithLifecycle()
    val sueno = suenos.find { it.id == id }

    // Diálogo de confirmación antes de borrar
    var mostrarConfirmacionBorrar by remember { mutableStateOf(false) }

    if (sueno == null) {
        // El sueño no existe, volvemos atrás
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
                    // Botón para eliminar el sueño
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Fecha y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(fechaTexto, color = TextoApagado, fontSize = 12.sp)
                EstadoBadgeDetalle(estado = sueno.estado)
            }

            // Contenido según el estado del sueño
            when (sueno.estado) {
                EstadoSueno.RECORDADO -> ContenidoRecordado(sueno, etiquetas)
                EstadoSueno.NO_RECUERDO -> {
                    TarjetaInfo("🌫️ Soñaste pero no recuerdas nada de este sueño.")
                }
                EstadoSueno.NO_HE_SONADO -> {
                    TarjetaInfo("😴 No soñaste esta noche.")
                }
            }

            // Qué causó el despertar
            if (sueno.causasDespertar.isNotEmpty()) {
                SeccionDetalle(titulo = "Te despertó") {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        sueno.causasDespertar.forEach { causa ->
                            ChipDetalle(texto = textoDespertar(causa))
                        }
                    }
                }
            }

            // Contexto de la noche
            sueno.contextoNoche?.let { contexto ->
                SeccionDetalle(titulo = "Contexto de la noche") {
                    ContextoDetalle(contexto, ubicaciones)
                }
            }
        }
    }

    // Diálogo para confirmar el borrado
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

// Contenido completo cuando el sueño fue recordado
@Composable
private fun ContenidoRecordado(
    sueno: Sueno,
    etiquetas: List<com.ernesto.atrapasomnins.data.model.Etiqueta>
) {
    // Descripción del sueño
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

    // Intensidad y si fue lúcido
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

    // Etiquetas del sueño
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

// Muestra los datos del contexto de la noche anterior
@Composable
private fun ContextoDetalle(
    contexto: ContextoNoche,
    ubicaciones: List<com.ernesto.atrapasomnins.data.model.Ubicacion>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Compañero
        contexto.companero?.let {
            FilaDato("Con quién", it.replaceFirstChar { c -> c.uppercase() })
        }
        // Cansancio y estrés
        contexto.cansancio?.let {
            FilaDato("Cansancio", "★".repeat(it) + "☆".repeat(5 - it))
        }
        contexto.estres?.let {
            FilaDato("Estrés", "★".repeat(it) + "☆".repeat(5 - it))
        }
        // Hábitos
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
        // Sustancias
        contexto.alcohol?.let {
            FilaDato("Alcohol", if (it) "Sí" else "No")
        }
        contexto.melatonina?.let {
            FilaDato("Melatonina", if (it) "Sí" else "No")
        }
        // Temperatura
        contexto.temperatura?.let {
            val texto = when (it) {
                "frio" -> "🥶 Frío"
                "calor" -> "🥵 Calor"
                else -> "😊 Normal"
            }
            FilaDato("Temperatura", texto)
        }
        // Ubicación
        contexto.ubicacionId?.let { id ->
            val ubi = ubicaciones.find { it.id == id }
            if (ubi != null) FilaDato("Ubicación", "📍 ${ubi.nombre}")
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

// Convierte el id de causa despertar a texto legible
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
