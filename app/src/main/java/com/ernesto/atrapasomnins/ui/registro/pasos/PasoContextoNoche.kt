package com.ernesto.atrapasomnins.ui.registro.pasos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
fun PasoContextoNoche(viewModel: RegistroViewModel) {
    val etiquetasCompanero by viewModel.etiquetasCompanero.collectAsStateWithLifecycle()
    val etiquetasLugar by viewModel.etiquetasLugar.collectAsStateWithLifecycle()
    val etiquetasSustancia by viewModel.etiquetasSustancia.collectAsStateWithLifecycle()
    val companeroSeleccionado by viewModel.companeroSeleccionado.collectAsStateWithLifecycle()
    val lugarSeleccionado by viewModel.lugarSeleccionado.collectAsStateWithLifecycle()
    val sustanciasSeleccionadas by viewModel.sustanciasSeleccionadas.collectAsStateWithLifecycle()
    val cansancio by viewModel.cansancio.collectAsStateWithLifecycle()
    val estres by viewModel.estres.collectAsStateWithLifecycle()
    val comidaPesada by viewModel.comidaPesada.collectAsStateWithLifecycle()
    val pantallas by viewModel.pantallas.collectAsStateWithLifecycle()
    val lectura by viewModel.lectura.collectAsStateWithLifecycle()
    val ejercicio by viewModel.ejercicio.collectAsStateWithLifecycle()
    val temperatura by viewModel.temperatura.collectAsStateWithLifecycle()

    // Estados locales para los diálogos de nueva etiqueta
    var mostrarDialogoCompanero by remember { mutableStateOf(false) }
    var mostrarDialogoLugar by remember { mutableStateOf(false) }
    var mostrarDialogoSustancia by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            "La noche anterior",
            color = TextoPrincipal,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        // ── Con quién dormiste ────────────────────────────────
        SeccionContexto(titulo = "¿Con quién dormiste?") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                etiquetasCompanero.forEach { etiqueta ->
                    val sel = etiqueta.id in companeroSeleccionado
                    FilterChip(
                        selected = sel,
                        onClick = {
                            viewModel.companeroSeleccionado.value =
                                if (sel) emptySet()
                                else setOf(etiqueta.id)
                        },
                        label = { Text(etiqueta.nombre) },
                        colors = chipColors(sel),
                        border = chipBorder(sel)
                    )
                }
                // Botón + para añadir compañero personalizado
                InputChip(
                    selected = false,
                    onClick = { mostrarDialogoCompanero = true },
                    label = { Text("+") },
                    colors = InputChipDefaults.inputChipColors(
                        containerColor = AzulNocheMedio,
                        labelColor = LilaClaro
                    ),
                    border = InputChipDefaults.inputChipBorder(
                        enabled = true, selected = false,
                        borderColor = LilaClaro.copy(alpha = 0.5f)
                    )
                )
            }
        }

        // ── Dónde dormiste ────────────────────────────────────
        SeccionContexto(titulo = "¿Dónde dormiste?") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                etiquetasLugar.forEach { etiqueta ->
                    val sel = etiqueta.id in lugarSeleccionado
                    FilterChip(
                        selected = sel,
                        onClick = {
                            viewModel.lugarSeleccionado.value =
                                if (sel) emptySet()
                                else setOf(etiqueta.id)
                        },
                        label = { Text(etiqueta.nombre) },
                        colors = chipColors(sel),
                        border = chipBorder(sel)
                    )
                }
                InputChip(
                    selected = false,
                    onClick = { mostrarDialogoLugar = true },
                    label = { Text("+") },
                    colors = InputChipDefaults.inputChipColors(
                        containerColor = AzulNocheMedio,
                        labelColor = LilaClaro
                    ),
                    border = InputChipDefaults.inputChipBorder(
                        enabled = true, selected = false,
                        borderColor = LilaClaro.copy(alpha = 0.5f)
                    )
                )
            }
        }

        // ── Cansancio ─────────────────────────────────────────
        SeccionContexto(titulo = "Cansancio al acostarte") {
            SelectorEstrellas(valor = cansancio, onCambio = { viewModel.cansancio.value = it })
        }

        // ── Estrés ────────────────────────────────────────────
        SeccionContexto(titulo = "Estrés del día") {
            SelectorEstrellas(valor = estres, onCambio = { viewModel.estres.value = it })
        }

        // ── Hábitos ───────────────────────────────────────────
        SeccionContexto(titulo = "Hábitos de ayer") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FilaSiNo("¿Comida pesada?", comidaPesada) { viewModel.comidaPesada.value = it }
                FilaSiNo("¿Pantallas antes de dormir?", pantallas) { viewModel.pantallas.value = it }
                FilaSiNo("¿Lectura antes de dormir?", lectura) { viewModel.lectura.value = it }
                FilaSiNo("¿Ejercicio ese día?", ejercicio) { viewModel.ejercicio.value = it }
            }
        }

        // ── Sustancias ────────────────────────────────────────
        SeccionContexto(titulo = "Sustancias") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                etiquetasSustancia.forEach { etiqueta ->
                    val sel = etiqueta.id in sustanciasSeleccionadas
                    FilterChip(
                        selected = sel,
                        onClick = {
                            viewModel.sustanciasSeleccionadas.value =
                                if (sel) sustanciasSeleccionadas - etiqueta.id
                                else sustanciasSeleccionadas + etiqueta.id
                        },
                        label = { Text(etiqueta.nombre) },
                        colors = chipColors(sel),
                        border = chipBorder(sel)
                    )
                }
                InputChip(
                    selected = false,
                    onClick = { mostrarDialogoSustancia = true },
                    label = { Text("+") },
                    colors = InputChipDefaults.inputChipColors(
                        containerColor = AzulNocheMedio,
                        labelColor = LilaClaro
                    ),
                    border = InputChipDefaults.inputChipBorder(
                        enabled = true, selected = false,
                        borderColor = LilaClaro.copy(alpha = 0.5f)
                    )
                )
            }
        }

        // ── Temperatura ───────────────────────────────────────
        SeccionContexto(titulo = "Temperatura en la habitación") {
            val opciones = listOf(
                "frio" to "🥶 Frío",
                "fresco" to "❄️ Fresco",
                "normal" to "😊 Normal",
                "calido" to "🌤️ Cálido",
                "calor" to "🥵 Calor"
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                opciones.forEach { (id, texto) ->
                    FilterChip(
                        selected = temperatura == id,
                        onClick = {
                            viewModel.temperatura.value = if (temperatura == id) null else id
                        },
                        label = { Text(texto) },
                        colors = chipColors(temperatura == id),
                        border = chipBorder(temperatura == id)
                    )
                }
            }
        }

        BotonSiguiente(onClick = { viewModel.avanzar() }, texto = "Guardar sueño ✓")
        Spacer(Modifier.height(16.dp))
    }

    // ── Diálogos de nueva etiqueta ────────────────────────────

    if (mostrarDialogoCompanero) {
        DialogoNuevaEtiqueta(
            onConfirmar = { nombre ->
                viewModel.crearYSeleccionarEtiqueta(
                    nombre, CategoriaEtiqueta.COMPANERO, viewModel.companeroSeleccionado
                )
                mostrarDialogoCompanero = false
            },
            onCancelar = { mostrarDialogoCompanero = false }
        )
    }

    if (mostrarDialogoLugar) {
        DialogoNuevaEtiqueta(
            onConfirmar = { nombre ->
                viewModel.crearYSeleccionarEtiqueta(
                    nombre, CategoriaEtiqueta.LUGAR, viewModel.lugarSeleccionado
                )
                mostrarDialogoLugar = false
            },
            onCancelar = { mostrarDialogoLugar = false }
        )
    }

    if (mostrarDialogoSustancia) {
        DialogoNuevaEtiqueta(
            onConfirmar = { nombre ->
                viewModel.crearYSeleccionarEtiqueta(
                    nombre, CategoriaEtiqueta.SUSTANCIA, viewModel.sustanciasSeleccionadas
                )
                mostrarDialogoSustancia = false
            },
            onCancelar = { mostrarDialogoSustancia = false }
        )
    }
}

// Diálogo reutilizable para crear una etiqueta nueva
@Composable
private fun DialogoNuevaEtiqueta(
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var texto by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onCancelar,
        containerColor = AzulNocheMedio,
        title = {
            Text("Nueva opción", color = TextoPrincipal, fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                placeholder = {
                    Text("Nombre...", color = TextoApagado.copy(alpha = 0.5f))
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
            TextButton(onClick = { if (texto.isNotBlank()) onConfirmar(texto.trim()) }) {
                Text("Añadir", color = LilaClaro)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar", color = TextoApagado)
            }
        }
    )
}

// ── Componentes auxiliares del contexto ─────────────────────

@Composable
private fun SeccionContexto(
    titulo: String,
    contenido: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(titulo, color = TextoPrincipal, fontWeight = FontWeight.Medium, fontSize = 15.sp)
        contenido()
        HorizontalDivider(color = TextoApagado.copy(alpha = 0.15f))
    }
}

@Composable
private fun FilaSiNo(
    texto: String,
    valor: Boolean?,
    onChange: (Boolean?) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(texto, color = TextoApagado, fontSize = 14.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            FilaBotonOpcion("Sí", valor == true, { onChange(if (valor == true) null else true) })
            FilaBotonOpcion("No", valor == false, { onChange(if (valor == false) null else false) })
        }
    }
}

@Composable
private fun FilaBotonOpcion(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
        modifier = Modifier.height(32.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (seleccionado) Morado.copy(alpha = 0.3f)
                             else androidx.compose.ui.graphics.Color.Transparent,
            contentColor = if (seleccionado) LilaClaro else TextoApagado
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (seleccionado) Morado else TextoApagado.copy(alpha = 0.3f)
        )
    ) {
        Text(texto, fontSize = 13.sp)
    }
}

@Composable
private fun SelectorEstrellas(valor: Int, onCambio: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        (1..5).forEach { nivel ->
            IconButton(
                onClick = { onCambio(nivel) },
                modifier = Modifier.size(36.dp)
            ) {
                Text(
                    text = "★",
                    fontSize = 24.sp,
                    color = if (nivel <= valor) LilaClaro
                            else TextoApagado.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun chipColors(seleccionado: Boolean) = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Morado,
    selectedLabelColor = TextoPrincipal,
    containerColor = AzulNocheMedio,
    labelColor = TextoApagado
)

@Composable
private fun chipBorder(seleccionado: Boolean) = FilterChipDefaults.filterChipBorder(
    enabled = true,
    selected = seleccionado,
    borderColor = TextoApagado.copy(alpha = 0.3f),
    selectedBorderColor = Morado
)
