// Colores de la app, inspirados en el cielo nocturno
package com.ernesto.atrapasomnins.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de colores nocturna
val AzulNoche = Color(0xFF0D0D1A)
val AzulNocheMedio = Color(0xFF1A1A2E)
val Morado = Color(0xFF7C5CBF)
val LilaClaro = Color(0xFFC89CFF)
val TextoPrincipal = Color(0xFFF0EEFF)
val TextoApagado = Color(0xFF6B6880)
val VerdeExito = Color(0xFF4CAF50)
val RojoError = Color(0xFFF44336)
val AmarilloAviso = Color(0xFFFFC107)

// Esquema de colores oscuro para Material3
private val EsquemaColores = darkColorScheme(
    primary = Morado,
    onPrimary = TextoPrincipal,
    primaryContainer = AzulNocheMedio,
    onPrimaryContainer = LilaClaro,
    background = AzulNoche,
    onBackground = TextoPrincipal,
    surface = AzulNocheMedio,
    onSurface = TextoPrincipal,
    surfaceVariant = Color(0xFF2A2A3E),
    onSurfaceVariant = TextoApagado,
    secondary = LilaClaro,
    onSecondary = AzulNoche,
    outline = Color(0xFF3A3A55)
)

@Composable
fun AtrapaSomnisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaColores,
        content = content
    )
}
