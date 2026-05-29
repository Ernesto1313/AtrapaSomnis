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
fun PasoTitulo(viewModel: RegistroViewModel) {
    val texto by viewModel.titulo.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Dale un nombre",
            color = TextoPrincipal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Un título corto para recordarlo rápido",
            color = TextoApagado,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = texto,
            onValueChange = { viewModel.titulo.value = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    "El dragón azul, La casa infinita...",
                    color = TextoApagado.copy(alpha = 0.5f)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
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

        Spacer(Modifier.weight(1f))
        BotonSiguiente(
            onClick = { viewModel.avanzar() },
            habilitado = texto.isNotBlank()
        )
    }
}
