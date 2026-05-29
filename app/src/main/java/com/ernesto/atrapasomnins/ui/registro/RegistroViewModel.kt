// ViewModel exclusivo para el flujo de registro de un sueño nuevo.
// Acumula los datos paso a paso hasta que el usuario lo guarda todo.

package com.ernesto.atrapasomnins.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ernesto.atrapasomnins.data.model.ContextoNoche
import com.ernesto.atrapasomnins.data.model.EstadoSueno
import com.ernesto.atrapasomnins.data.model.Etiqueta
import com.ernesto.atrapasomnins.data.model.Sueno
import com.ernesto.atrapasomnins.data.model.Ubicacion
import com.ernesto.atrapasomnins.data.repository.EtiquetaRepository
import com.ernesto.atrapasomnins.data.repository.SuenoRepository
import com.ernesto.atrapasomnins.data.repository.UbicacionRepository
import com.ernesto.atrapasomnins.data.location.LocationDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

// Pasos del flujo de registro al despertar
enum class PasoRegistro {
    ESTADO_SUENO,       // ¿Has soñado?
    DESCRIPCION,        // Descripción libre del sueño
    TITULO,             // Título corto
    LUCIDO_INTENSIDAD,  // ¿Fue lúcido? + intensidad 1-5
    ETIQUETAS,          // Etiquetas del sueño
    CAUSA_DESPERTAR,    // Qué te despertó
    CONTEXTO_NOCHE      // Info de la noche anterior
}

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val suenoRepository: SuenoRepository,
    private val etiquetaRepository: EtiquetaRepository,
    private val ubicacionRepository: UbicacionRepository,
    private val locationDataSource: LocationDataSource
) : ViewModel() {

    // Paso actual del flujo
    private val _pasoActual = MutableStateFlow(PasoRegistro.ESTADO_SUENO)
    val pasoActual: StateFlow<PasoRegistro> = _pasoActual.asStateFlow()

    // Etiquetas disponibles para seleccionar
    private val _etiquetas = MutableStateFlow<List<Etiqueta>>(emptyList())
    val etiquetas: StateFlow<List<Etiqueta>> = _etiquetas.asStateFlow()

    // Ubicaciones guardadas para mostrar opciones
    private val _ubicaciones = MutableStateFlow<List<Ubicacion>>(emptyList())
    val ubicaciones: StateFlow<List<Ubicacion>> = _ubicaciones.asStateFlow()

    // Coordenadas GPS actuales
    private val _coordenadas = MutableStateFlow<Pair<Double, Double>?>(null)
    val coordenadas: StateFlow<Pair<Double, Double>?> = _coordenadas.asStateFlow()

    // ── Datos del sueño que vamos acumulando ────────────────

    var estadoSueno = MutableStateFlow(EstadoSueno.RECORDADO)
    var descripcion = MutableStateFlow("")
    var titulo = MutableStateFlow("")
    var esLucido = MutableStateFlow(false)
    var intensidad = MutableStateFlow(3)
    var etiquetasSeleccionadas = MutableStateFlow<Set<String>>(emptySet())
    var causasDespertar = MutableStateFlow<Set<String>>(emptySet())

    // ── Contexto de la noche anterior ───────────────────────
    var companero = MutableStateFlow<String?>(null)
    var cansancio = MutableStateFlow(3)
    var estres = MutableStateFlow(3)
    var comidaPesada = MutableStateFlow<Boolean?>(null)
    var pantallas = MutableStateFlow<Boolean?>(null)
    var lectura = MutableStateFlow<Boolean?>(null)
    var ejercicio = MutableStateFlow<Boolean?>(null)
    var alcohol = MutableStateFlow<Boolean?>(null)
    var otrasSustancias = MutableStateFlow<Boolean?>(null)
    var intensidadSustancias = MutableStateFlow(1)
    var etiquetasSustancias = MutableStateFlow<Set<String>>(emptySet())
    var melatonina = MutableStateFlow<Boolean?>(null)
    var otrosSomniferos = MutableStateFlow("")
    var temperatura = MutableStateFlow<String?>(null)
    var ubicacionSeleccionadaId = MutableStateFlow<String?>(null)
    var nuevaUbicacionNombre = MutableStateFlow("")

    // Indica si el registro ha terminado y hay que volver al inicio
    private val _registroCompletado = MutableStateFlow(false)
    val registroCompletado: StateFlow<Boolean> = _registroCompletado.asStateFlow()

    init {
        viewModelScope.launch {
            _etiquetas.value = etiquetaRepository.obtenerTodas()
            _ubicaciones.value = ubicacionRepository.obtenerTodas()
            // Obtenemos la ubicación GPS al abrir el registro
            _coordenadas.value = locationDataSource.obtenerUbicacionActual()
            // Intentamos asignar automáticamente una ubicación conocida
            _coordenadas.value?.let { (lat, lon) ->
                val cercana = ubicacionRepository.encontrarCercana(lat, lon)
                if (cercana != null) {
                    ubicacionSeleccionadaId.value = cercana.id
                }
            }
        }
    }

    // Avanza al siguiente paso según el estado del sueño
    fun avanzar() {
        _pasoActual.value = when (_pasoActual.value) {
            PasoRegistro.ESTADO_SUENO -> {
                // Si no ha soñado o no recuerda, saltamos directo al contexto
                if (estadoSueno.value == EstadoSueno.RECORDADO) {
                    PasoRegistro.DESCRIPCION
                } else {
                    PasoRegistro.CAUSA_DESPERTAR
                }
            }
            PasoRegistro.DESCRIPCION -> PasoRegistro.TITULO
            PasoRegistro.TITULO -> PasoRegistro.LUCIDO_INTENSIDAD
            PasoRegistro.LUCIDO_INTENSIDAD -> PasoRegistro.ETIQUETAS
            PasoRegistro.ETIQUETAS -> PasoRegistro.CAUSA_DESPERTAR
            PasoRegistro.CAUSA_DESPERTAR -> PasoRegistro.CONTEXTO_NOCHE
            PasoRegistro.CONTEXTO_NOCHE -> {
                // Último paso: guardamos y terminamos
                guardarSueno()
                PasoRegistro.CONTEXTO_NOCHE
            }
        }
    }

    // Retrocede al paso anterior
    fun retroceder(): Boolean {
        val anterior = when (_pasoActual.value) {
            PasoRegistro.ESTADO_SUENO -> return false // Ya estamos al principio
            PasoRegistro.DESCRIPCION -> PasoRegistro.ESTADO_SUENO
            PasoRegistro.TITULO -> PasoRegistro.DESCRIPCION
            PasoRegistro.LUCIDO_INTENSIDAD -> PasoRegistro.TITULO
            PasoRegistro.ETIQUETAS -> PasoRegistro.LUCIDO_INTENSIDAD
            PasoRegistro.CAUSA_DESPERTAR -> {
                if (estadoSueno.value == EstadoSueno.RECORDADO) {
                    PasoRegistro.ETIQUETAS
                } else {
                    PasoRegistro.ESTADO_SUENO
                }
            }
            PasoRegistro.CONTEXTO_NOCHE -> PasoRegistro.CAUSA_DESPERTAR
        }
        _pasoActual.value = anterior
        return true
    }

    // Crea una etiqueta nueva y la selecciona automáticamente
    fun crearYSeleccionarEtiqueta(nombre: String) {
        viewModelScope.launch {
            val nueva = etiquetaRepository.crearEtiqueta(nombre)
            _etiquetas.value = etiquetaRepository.obtenerTodas()
            // La añadimos a las seleccionadas
            etiquetasSeleccionadas.value = etiquetasSeleccionadas.value + nueva.id
        }
    }

    // Guarda una nueva ubicación con nombre y la selecciona
    fun guardarNuevaUbicacion(nombre: String) {
        val coords = _coordenadas.value ?: return
        viewModelScope.launch {
            val nueva = Ubicacion(
                id = UUID.randomUUID().toString(),
                nombre = nombre,
                latitud = coords.first,
                longitud = coords.second
            )
            ubicacionRepository.guardar(nueva)
            _ubicaciones.value = ubicacionRepository.obtenerTodas()
            ubicacionSeleccionadaId.value = nueva.id
        }
    }

    // Construye el objeto Sueno con todos los datos acumulados y lo guarda
    private fun guardarSueno() {
        viewModelScope.launch {
            val contexto = ContextoNoche(
                companero = companero.value,
                cansancio = cansancio.value,
                estres = estres.value,
                comidaPesada = comidaPesada.value,
                pantallas = pantallas.value,
                lectura = lectura.value,
                ejercicio = ejercicio.value,
                alcohol = alcohol.value,
                otrasSustancias = otrasSustancias.value,
                intensidadSustancias = if (otrasSustancias.value == true)
                    intensidadSustancias.value else null,
                etiquetasSustancias = etiquetasSustancias.value.toList(),
                melatonina = melatonina.value,
                otrosSomniferos = otrosSomniferos.value.ifBlank { null },
                temperatura = temperatura.value,
                ubicacionId = ubicacionSeleccionadaId.value,
                latitudRaw = _coordenadas.value?.first,
                longitudRaw = _coordenadas.value?.second
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
