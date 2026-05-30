package com.ernesto.atrapasomnins.ui.estadisticas

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ernesto.atrapasomnins.data.model.EstadoSueno
import com.ernesto.atrapasomnins.ui.SuenoViewModel
import com.ernesto.atrapasomnins.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstadisticasScreen(
    viewModel: SuenoViewModel = hiltViewModel()
) {
    val suenos by viewModel.suenos.collectAsStateWithLifecycle()
    val etiquetas by viewModel.etiquetas.collectAsStateWithLifecycle()

    // Calculamos todas las estadísticas a partir de la lista de sueños
    val totalNoches = suenos.size
    val recordados = suenos.count { it.estado == EstadoSueno.RECORDADO }
    val noRecuerda = suenos.count { it.estado == EstadoSueno.NO_RECUERDO }
    val sinSueno = suenos.count { it.estado == EstadoSueno.NO_HE_SONADO }
    val lucidos = suenos.count { it.lucido == true }

    // Porcentaje de noches con sueño recordado
    val porcentajeRecordados = if (totalNoches > 0)
        (recordados * 100f / totalNoches).toInt() else 0

    // Etiqueta más usada entre todos los sueños
    val etiquetaMasUsada = suenos
        .flatMap { it.etiquetas }
        .groupingBy { it }
        .eachCount()
        .maxByOrNull { it.value }
        ?.let { (idEtiqueta, veces) ->
            val nombre = etiquetas.find { it.id == idEtiqueta }?.nombre ?: idEtiqueta
            "$nombre ($veces veces)"
        } ?: "—"

    // Intensidad media de los sueños recordados
    val intensidadMedia = suenos
        .mapNotNull { it.intensidad }
        .let { lista ->
            if (lista.isEmpty()) null
            else lista.average()
        }

    Scaffold(
        containerColor = AzulNoche,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Estadísticas",
                        color = LilaClaro,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AzulNocheMedio
                )
            )
        }
    ) { padding ->
        if (totalNoches == 0) {
            // Pantalla vacía si no hay datos todavía
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = TextoApagado,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        "Registra tus primeros sueños\npara ver estadísticas",
                        color = TextoApagado,
                        fontSize = 15.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tarjeta principal con el porcentaje de recuerdo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AzulNocheMedio)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$porcentajeRecordados%",
                        color = LilaClaro,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "de noches recuerdas tus sueños",
                        color = TextoApagado,
                        fontSize = 14.sp
                    )
                    LinearProgressIndicator(
                        progress = { porcentajeRecordados / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = Morado,
                        trackColor = AzulNoche
                    )
                }
            }

            // Cuadrícula 2x2 con datos rápidos
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaStat(
                    titulo = "Total noches",
                    valor = "$totalNoches",
                    modifier = Modifier.weight(1f)
                )
                TarjetaStat(
                    titulo = "Sueños lúcidos",
                    valor = "$lucidos",
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TarjetaStat(
                    titulo = "Sin recuerdo",
                    valor = "$noRecuerda",
                    modifier = Modifier.weight(1f)
                )
                TarjetaStat(
                    titulo = "Sin sueño",
                    valor = "$sinSueno",
                    modifier = Modifier.weight(1f)
                )
            }

            // Etiqueta más frecuente
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AzulNocheMedio)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Etiqueta más frecuente", color = TextoApagado, fontSize = 12.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(etiquetaMasUsada, color = LilaClaro, fontWeight = FontWeight.SemiBold)
                }
            }

            // Intensidad media
            if (intensidadMedia != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulNocheMedio)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Intensidad media", color = TextoApagado, fontSize = 12.sp)
                        Spacer(Modifier.height(4.dp))
                        Row {
                            val redondeado = intensidadMedia.toInt()
                            repeat(redondeado) {
                                Text("★", color = LilaClaro, fontSize = 18.sp)
                            }
                            repeat(5 - redondeado) {
                                Text("★", color = TextoApagado.copy(alpha = 0.3f), fontSize = 18.sp)
                            }
                            Text(
                                " (${"%.1f".format(intensidadMedia)})",
                                color = TextoApagado,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaStat(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AzulNocheMedio)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(titulo, color = TextoApagado, fontSize = 12.sp)
            Text(valor, color = TextoPrincipal, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

