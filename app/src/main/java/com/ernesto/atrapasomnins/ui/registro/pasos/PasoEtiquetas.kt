package com.ernesto.atrapasomnins.ui.registro.pasos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ernesto.atrapasomnins.data.model.CategoriaEtiqueta
import com.ernesto.atrapasomnins.ui.registro.RegistroViewModel
import com.ernesto.atrapasomnins.ui.theme.*

@Composable
fun PasoEtiquetas(viewModel: RegistroViewModel) {
    val etiquetas by viewModel.etiquetasSueno.collectAsStateWithLifecycle()
    val seleccionadas by viewModel.etiquetasSeleccionadas.collectAsStateWithLifecycle()

    // Estado local para el diálogo de nueva etiqueta
    var mostrarDialogo by remember { mutableStateOf(false) }
    var textoNuevaEtiqueta by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "¿Cómo clasificarías este sueño?",
            color = TextoPrincipal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Puedes seleccionar varias etiquetas",
            color = TextoApagado,
            fontSize = 14.sp
        )

        // Cuadrícula de etiquetas como chips seleccionables
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            etiquetas.forEach { etiqueta ->
                val seleccionada = etiqueta.id in seleccionadas
                FilterChip(
                    selected = seleccionada,
                    onClick = {
                        // Añadir o quitar de la selección
                        viewModel.etiquetasSeleccionadas.value =
                            if (seleccionada) seleccionadas - etiqueta.id
                            else seleccionadas + etiqueta.id
                    },
                    label = { Text(etiqueta.nombre) },
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

            // Chip especial para crear una etiqueta nueva in-situ
            InputChip(
                selected = false,
                onClick = { mostrarDialogo = true },
                label = { Text("Nueva etiqueta") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = InputChipDefaults.inputChipColors(
                    containerColor = AzulNocheMedio,
                    labelColor = LilaClaro
                ),
                border = InputChipDefaults.inputChipBorder(
                    enabled = true,
                    selected = false,
                    borderColor = LilaClaro.copy(alpha = 0.5f)
                )
            )
        }

        Spacer(Modifier.height(8.dp))
        // Se puede saltar este paso si no quieres etiquetar
        BotonSiguiente(
            onClick = { viewModel.avanzar() },
            texto = if (seleccionadas.isEmpty()) "Saltar →" else "Siguiente →"
        )
    }

    // Diálogo para crear una etiqueta nueva
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogo = false
                textoNuevaEtiqueta = ""
            },
            containerColor = AzulNocheMedio,
            title = {
                Text("Nueva etiqueta", color = TextoPrincipal, fontWeight = FontWeight.Bold)
            },
            text = {
                OutlinedTextField(
                    value = textoNuevaEtiqueta,
                    onValueChange = { textoNuevaEtiqueta = it },
                    placeholder = {
                        Text("Nombre de la etiqueta", color = TextoApagado.copy(alpha = 0.5f))
                    },
                    singleLine = true,
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
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (textoNuevaEtiqueta.isNotBlank()) {
                            viewModel.crearYSeleccionarEtiqueta(
                                textoNuevaEtiqueta.trim(),
                                CategoriaEtiqueta.SUENO,
                                viewModel.etiquetasSeleccionadas
                            )
                            textoNuevaEtiqueta = ""
                            mostrarDialogo = false
                        }
                    }
                ) {
                    Text("Crear", color = LilaClaro)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogo = false
                    textoNuevaEtiqueta = ""
                }) {
                    Text("Cancelar", color = TextoApagado)
                }
            }
        )
    }
}
