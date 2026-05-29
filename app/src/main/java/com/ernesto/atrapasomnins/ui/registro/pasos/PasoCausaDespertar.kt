package com.ernesto.atrapasomnins.ui.registro.pasos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ernesto.atrapasomnins.ui.registro.RegistroViewModel
import com.ernesto.atrapasomnins.ui.theme.*

@Composable
fun PasoCausaDespertar(viewModel: RegistroViewModel) {
    val seleccionadas by viewModel.causasDespertar.collectAsStateWithLifecycle()

    // Opciones disponibles con emoji para hacerlo más visual
    val opciones = listOf(
        "alarma" to "⏰  Alarma",
        "luz" to "☀️  Luz",
        "ruido" to "🔊  Ruido",
        "sueno_propio" to "💭  El propio sueño",
        "alguien" to "🙋  Alguien me despertó",
        "nada" to "✨  Nada, solo me desperté",
        "llamada" to "📞  Una llamada",
        "timbre" to "🔔  El timbre",
        "sed_calor_frio" to "🌡️  Sed, calor o frío",
        "solo" to "🌅  Solo me desperté"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "¿Qué te despertó?",
            color = TextoPrincipal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Puedes seleccionar varias opciones",
            color = TextoApagado,
            fontSize = 14.sp
        )

        // Chips de selección múltiple
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opciones.forEach { (id, texto) ->
                val seleccionada = id in seleccionadas
                FilterChip(
                    selected = seleccionada,
                    onClick = {
                        viewModel.causasDespertar.value =
                            if (seleccionada) seleccionadas - id
                            else seleccionadas + id
                    },
                    label = { Text(texto) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Morado,
                        selectedLabelColor = TextoPrincipal,
                        containerColor = AzulNocheMedio,
                        labelColor = TextoApagado
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = seleccionada,
                        borderColor = TextoApagado.copy(alpha = 0.3f),
                        selectedBorderColor = Morado
                    )
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        BotonSiguiente(
            onClick = { viewModel.avanzar() },
            texto = if (seleccionadas.isEmpty()) "Saltar →" else "Siguiente →"
        )
    }
}
