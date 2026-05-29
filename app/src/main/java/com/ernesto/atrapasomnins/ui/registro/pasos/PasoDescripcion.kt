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
fun PasoDescripcion(viewModel: RegistroViewModel) {
    val texto by viewModel.descripcion.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Describe tu sueño",
            color = TextoPrincipal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Escribe todo lo que recuerdes, sin filtros",
            color = TextoApagado,
            fontSize = 14.sp
        )

        // Campo de texto grande para la descripción libre
        OutlinedTextField(
            value = texto,
            onValueChange = { viewModel.descripcion.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text(
                    "Estaba en un lugar extraño y de repente...",
                    color = TextoApagado.copy(alpha = 0.5f)
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Morado,
                unfocusedBorderColor = TextoApagado.copy(alpha = 0.3f),
                focusedTextColor = TextoPrincipal,
                unfocusedTextColor = TextoPrincipal,
                cursorColor = LilaClaro,
                focusedContainerColor = AzulNocheMedio,
                unfocusedContainerColor = AzulNocheMedio
            ),
            minLines = 5
        )

        BotonSiguiente(
            onClick = { viewModel.avanzar() },
            habilitado = texto.isNotBlank()
        )
    }
}
