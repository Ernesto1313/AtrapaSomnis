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
import com.ernesto.atrapasomnins.data.model.EstadoSueno
import com.ernesto.atrapasomnins.ui.registro.RegistroViewModel
import com.ernesto.atrapasomnins.ui.theme.*

@Composable
fun PasoEstadoSueno(viewModel: RegistroViewModel) {
    val estadoActual by viewModel.estadoSueno.collectAsStateWithLifecycle()

    // Las tres opciones que puede elegir el usuario
    val opciones = listOf(
        Triple(EstadoSueno.RECORDADO, "Sí, y lo recuerdo", "✨"),
        Triple(EstadoSueno.NO_RECUERDO, "Sí, pero no recuerdo nada", "🌫️"),
        Triple(EstadoSueno.NO_HE_SONADO, "No he soñado", "😴")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¿Has soñado esta noche?",
            color = TextoPrincipal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(32.dp))

        opciones.forEach { (estado, texto, emoji) ->
            val seleccionado = estadoActual == estado
            OutlinedButton(
                onClick = { viewModel.estadoSueno.value = estado },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (seleccionado) Morado.copy(alpha = 0.3f)
                                     else AzulNocheMedio,
                    contentColor = if (seleccionado) LilaClaro else TextoApagado
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = if (seleccionado) 2.dp else 1.dp,
                    color = if (seleccionado) Morado else TextoApagado.copy(alpha = 0.3f)
                )
            ) {
                Text("$emoji  $texto", fontSize = 15.sp)
            }
        }

        Spacer(Modifier.height(32.dp))
        BotonSiguiente(onClick = { viewModel.avanzar() })
    }
}
