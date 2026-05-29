// Pantalla contenedora del flujo de registro.
// Muestra el paso correcto según el estado del ViewModel.

package com.ernesto.atrapasomnins.ui.registro

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ernesto.atrapasomnins.ui.theme.*
import com.ernesto.atrapasomnins.ui.registro.pasos.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onTerminar: () -> Unit,
    viewModel: RegistroViewModel = hiltViewModel()
) {
    val pasoActual by viewModel.pasoActual.collectAsStateWithLifecycle()
    val completado by viewModel.registroCompletado.collectAsStateWithLifecycle()

    // Cuando el registro termina, volvemos a la pantalla de inicio
    LaunchedEffect(completado) {
        if (completado) onTerminar()
    }

    // Interceptamos el botón atrás del sistema
    BackHandler {
        val pudoRetroceder = viewModel.retroceder()
        if (!pudoRetroceder) onTerminar()
    }

    // Calculamos el progreso de la barra según el paso actual
    val totalPasos = PasoRegistro.entries.size
    val pasoIndex = PasoRegistro.entries.indexOf(pasoActual)
    val progreso = (pasoIndex + 1).toFloat() / totalPasos

    Scaffold(
        containerColor = AzulNoche,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = tituloDelPaso(pasoActual),
                            color = TextoPrincipal,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            val pudoRetroceder = viewModel.retroceder()
                            if (!pudoRetroceder) onTerminar()
                        }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Atrás",
                                tint = LilaClaro
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = AzulNocheMedio
                    )
                )
                // Barra de progreso que indica en qué paso estamos
                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth(),
                    color = Morado,
                    trackColor = AzulNocheMedio
                )
            }
        }
    ) { padding ->
        // Animación de deslizamiento entre pasos
        AnimatedContent(
            targetState = pasoActual,
            transitionSpec = {
                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
            },
            label = "transicion_pasos"
        ) { paso ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(AzulNoche)
            ) {
                when (paso) {
                    PasoRegistro.ESTADO_SUENO -> PasoEstadoSueno(viewModel)
                    PasoRegistro.DESCRIPCION -> PasoDescripcion(viewModel)
                    PasoRegistro.TITULO -> PasoTitulo(viewModel)
                    PasoRegistro.LUCIDO_INTENSIDAD -> PasoLucidoIntensidad(viewModel)
                    PasoRegistro.ETIQUETAS -> PasoEtiquetas(viewModel)
                    PasoRegistro.CAUSA_DESPERTAR -> PasoCausaDespertar(viewModel)
                    PasoRegistro.CONTEXTO_NOCHE -> PasoContextoNoche(viewModel)
                }
            }
        }
    }
}

// Devuelve el título a mostrar en la TopBar según el paso
private fun tituloDelPaso(paso: PasoRegistro): String = when (paso) {
    PasoRegistro.ESTADO_SUENO -> "¿Has soñado?"
    PasoRegistro.DESCRIPCION -> "Cuéntame tu sueño"
    PasoRegistro.TITULO -> "Dale un nombre"
    PasoRegistro.LUCIDO_INTENSIDAD -> "¿Cómo fue?"
    PasoRegistro.ETIQUETAS -> "Etiquetas"
    PasoRegistro.CAUSA_DESPERTAR -> "¿Qué te despertó?"
    PasoRegistro.CONTEXTO_NOCHE -> "La noche anterior"
}
