// Componentes reutilizables entre los distintos pasos del registro

package com.ernesto.atrapasomnins.ui.registro.pasos

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ernesto.atrapasomnins.ui.theme.*

// Botón de "Siguiente" que se usa en todos los pasos
@Composable
fun BotonSiguiente(
    onClick: () -> Unit,
    texto: String = "Siguiente →",
    habilitado: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = habilitado,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Morado,
            contentColor = TextoPrincipal,
            disabledContainerColor = TextoApagado.copy(alpha = 0.2f),
            disabledContentColor = TextoApagado
        )
    ) {
        Text(texto, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}
