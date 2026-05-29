package com.ernesto.atrapasomnins.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ernesto.atrapasomnins.data.model.CategoriaEtiqueta
import com.ernesto.atrapasomnins.data.model.ContextoNoche
import com.ernesto.atrapasomnins.data.model.EstadoSueno
import com.ernesto.atrapasomnins.data.model.Etiqueta
import com.ernesto.atrapasomnins.data.model.Sueno
import com.ernesto.atrapasomnins.data.repository.EtiquetaRepository
import com.ernesto.atrapasomnins.data.repository.SuenoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

enum class PasoRegistro {
    ESTADO_SUENO,
    DESCRIPCION,
    TITULO,
    LUCIDO_INTENSIDAD,
    ETIQUETAS,
    CAUSA_DESPERTAR,
    CONTEXTO_NOCHE
}

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val suenoRepository: SuenoRepository,
    private val etiquetaRepository: EtiquetaRepository
) : ViewModel() {

    private val _pasoActual = MutableStateFlow(PasoRegistro.ESTADO_SUENO)
    val pasoActual: StateFlow<PasoRegistro> = _pasoActual.asStateFlow()

    // Etiquetas por categoría para mostrar en cada sección
    private val _etiquetasSueno = MutableStateFlow<List<Etiqueta>>(emptyList())
    val etiquetasSueno: StateFlow<List<Etiqueta>> = _etiquetasSueno.asStateFlow()

    private val _etiquetasCompanero = MutableStateFlow<List<Etiqueta>>(emptyList())
    val etiquetasCompanero: StateFlow<List<Etiqueta>> = _etiquetasCompanero.asStateFlow()

    private val _etiquetasLugar = MutableStateFlow<List<Etiqueta>>(emptyList())
    val etiquetasLugar: StateFlow<List<Etiqueta>> = _etiquetasLugar.asStateFlow()

    private val _etiquetasSustancia = MutableStateFlow<List<Etiqueta>>(emptyList())
    val etiquetasSustancia: StateFlow<List<Etiqueta>> = _etiquetasSustancia.asStateFlow()

    // ── Datos del sueño ─────────────────────────────────────
    var estadoSueno = MutableStateFlow(EstadoSueno.RECORDADO)
    var descripcion = MutableStateFlow("")
    var titulo = MutableStateFlow("")
    var esLucido = MutableStateFlow(false)
    var intensidad = MutableStateFlow(3)
    var etiquetasSeleccionadas = MutableStateFlow<Set<String>>(emptySet())
    var causasDespertar = MutableStateFlow<Set<String>>(emptySet())

    // ── Contexto de la noche ─────────────────────────────────
    var cansancio = MutableStateFlow(3)
    var estres = MutableStateFlow(3)
    var comidaPesada = MutableStateFlow<Boolean?>(null)
    var pantallas = MutableStateFlow<Boolean?>(null)
    var lectura = MutableStateFlow<Boolean?>(null)
    var ejercicio = MutableStateFlow<Boolean?>(null)
    var temperatura = MutableStateFlow<String?>(null)
    var companeroSeleccionado = MutableStateFlow<Set<String>>(emptySet())
    var lugarSeleccionado = MutableStateFlow<Set<String>>(emptySet())
    var sustanciasSeleccionadas = MutableStateFlow<Set<String>>(emptySet())

    private val _registroCompletado = MutableStateFlow(false)
    val registroCompletado: StateFlow<Boolean> = _registroCompletado.asStateFlow()

    init {
        cargarEtiquetas()
    }

    fun cargarEtiquetas() {
        viewModelScope.launch {
            _etiquetasSueno.value =
                etiquetaRepository.obtenerPorCategoria(CategoriaEtiqueta.SUENO)
            _etiquetasCompanero.value =
                etiquetaRepository.obtenerPorCategoria(CategoriaEtiqueta.COMPANERO)
            _etiquetasLugar.value =
                etiquetaRepository.obtenerPorCategoria(CategoriaEtiqueta.LUGAR)
            _etiquetasSustancia.value =
                etiquetaRepository.obtenerPorCategoria(CategoriaEtiqueta.SUSTANCIA)
        }
    }

    fun avanzar() {
        _pasoActual.value = when (_pasoActual.value) {
            PasoRegistro.ESTADO_SUENO ->
                if (estadoSueno.value == EstadoSueno.RECORDADO) PasoRegistro.DESCRIPCION
                else PasoRegistro.CAUSA_DESPERTAR
            PasoRegistro.DESCRIPCION -> PasoRegistro.TITULO
            PasoRegistro.TITULO -> PasoRegistro.LUCIDO_INTENSIDAD
            PasoRegistro.LUCIDO_INTENSIDAD -> PasoRegistro.ETIQUETAS
            PasoRegistro.ETIQUETAS -> PasoRegistro.CAUSA_DESPERTAR
            PasoRegistro.CAUSA_DESPERTAR -> PasoRegistro.CONTEXTO_NOCHE
            PasoRegistro.CONTEXTO_NOCHE -> {
                guardarSueno()
                PasoRegistro.CONTEXTO_NOCHE
            }
        }
    }

    fun retroceder(): Boolean {
        val anterior = when (_pasoActual.value) {
            PasoRegistro.ESTADO_SUENO -> return false
            PasoRegistro.DESCRIPCION -> PasoRegistro.ESTADO_SUENO
            PasoRegistro.TITULO -> PasoRegistro.DESCRIPCION
            PasoRegistro.LUCIDO_INTENSIDAD -> PasoRegistro.TITULO
            PasoRegistro.ETIQUETAS -> PasoRegistro.LUCIDO_INTENSIDAD
            PasoRegistro.CAUSA_DESPERTAR ->
                if (estadoSueno.value == EstadoSueno.RECORDADO) PasoRegistro.ETIQUETAS
                else PasoRegistro.ESTADO_SUENO
            PasoRegistro.CONTEXTO_NOCHE -> PasoRegistro.CAUSA_DESPERTAR
        }
        _pasoActual.value = anterior
        return true
    }

    // Crea una etiqueta nueva en la categoría indicada y la selecciona
    fun crearYSeleccionarEtiqueta(
        nombre: String,
        categoria: CategoriaEtiqueta,
        seleccionSet: MutableStateFlow<Set<String>>
    ) {
        viewModelScope.launch {
            val nueva = etiquetaRepository.crearEtiqueta(nombre, categoria)
            cargarEtiquetas()
            seleccionSet.value = seleccionSet.value + nueva.id
        }
    }

    private fun guardarSueno() {
        viewModelScope.launch {
            val contexto = ContextoNoche(
                cansancio = cansancio.value,
                estres = estres.value,
                comidaPesada = comidaPesada.value,
                pantallas = pantallas.value,
                lectura = lectura.value,
                ejercicio = ejercicio.value,
                temperatura = temperatura.value,
                etiquetasCompanero = companeroSeleccionado.value.toList(),
                etiquetasLugar = lugarSeleccionado.value.toList(),
                etiquetasSustancia = sustanciasSeleccionadas.value.toList()
            )
            val sueno = Sueno(
                id = UUID.randomUUID().toString(),
                fechaCreacion = System.currentTimeMillis(),
                estado = estadoSueno.value,
                titulo = titulo.value.ifBlank { null },
                descripcion = descripcion.value.ifBlank { null },
                etiquetas = etiquetasSeleccionadas.value.toList(),
                intensidad = if (estadoSueno.value == EstadoSueno.RECORDADO)
                    intensidad.value else null,
                lucido = if (estadoSueno.value == EstadoSueno.RECORDADO)
                    esLucido.value else null,
                causasDespertar = causasDespertar.value.toList(),
                contextoNoche = contexto
            )
            suenoRepository.guardar(sueno)
            _registroCompletado.value = true
        }
    }
}
