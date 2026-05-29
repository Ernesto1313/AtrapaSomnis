package com.ernesto.atrapasomnins.ui.registro.pasos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun PasoLucidoIntensidad(viewModel: RegistroViewModel) {
    val esLucido by viewModel.esLucido.collectAsStateWithLifecycle()
    val intensidad by viewModel.intensidad.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "¿Cómo fue el sueño?",
            color = TextoPrincipal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        // Pregunta de sueño lúcido
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AzulNocheMedio)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("¿Fue lúcido?", color = TextoPrincipal, fontWeight = FontWeight.Medium)
                    Text(
                        "Eras consciente de que soñabas",
                        color = TextoApagado,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = esLucido,
                    onCheckedChange = { viewModel.esLucido.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = LilaClaro,
                        checkedTrackColor = Morado
                    )
                )
            }
        }

        // Selector de intensidad del sueño (qué tan vívido fue)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Intensidad", color = TextoPrincipal, fontWeight = FontWeight.Medium)
                Text("$intensidad / 5", color = LilaClaro, fontWeight = FontWeight.Bold)
            }
            Text(
                text = "¿Qué tan vívido fue el sueño?",
                color = TextoApagado,
                fontSize = 12.sp
            )
            // Fila de 5 botones estrella para seleccionar la intensidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                (1..5).forEach { nivel ->
                    IconButton(onClick = { viewModel.intensidad.value = nivel }) {
                        Text(
                            text = "★",
                            fontSize = 32.sp,
                            color = if (nivel <= intensidad) LilaClaro
                                    else TextoApagado.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        BotonSiguiente(onClick = { viewModel.avanzar() })
    }
}
