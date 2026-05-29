// ViewModel principal de la app, compartido entre las pantallas de inicio,
// registro y detalle. Gestiona toda la lógica de negocio.

package com.ernesto.atrapasomnins.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject

@HiltViewModel
class SuenoViewModel @Inject constructor(
    private val suenoRepository: SuenoRepository,
    private val etiquetaRepository: EtiquetaRepository,
    private val ubicacionRepository: UbicacionRepository,
    private val locationDataSource: LocationDataSource
) : ViewModel() {

    // Lista de sueños para la pantalla de inicio
    private val _suenos = MutableStateFlow<List<Sueno>>(emptyList())
    val suenos: StateFlow<List<Sueno>> = _suenos.asStateFlow()

    // Lista de todas las etiquetas disponibles (predefinidas + personalizadas)
    private val _etiquetas = MutableStateFlow<List<Etiqueta>>(emptyList())
    val etiquetas: StateFlow<List<Etiqueta>> = _etiquetas.asStateFlow()

    // Ubicaciones guardadas por el usuario
    private val _ubicaciones = MutableStateFlow<List<Ubicacion>>(emptyList())
    val ubicaciones: StateFlow<List<Ubicacion>> = _ubicaciones.asStateFlow()

    // Etiqueta seleccionada para filtrar en la pantalla de inicio
    private val _filtroEtiqueta = MutableStateFlow<String?>(null)
    val filtroEtiqueta: StateFlow<String?> = _filtroEtiqueta.asStateFlow()

    // Ubicación GPS actual (puede ser null si no hay permiso)
    private val _ubicacionActual = MutableStateFlow<Pair<Double, Double>?>(null)
    val ubicacionActual: StateFlow<Pair<Double, Double>?> = _ubicacionActual.asStateFlow()

    init {
        // Cargamos todo al arrancar el ViewModel
        cargarDatos()
    }

    // Carga todos los datos del almacenamiento en paralelo
    fun cargarDatos() {
        viewModelScope.launch {
            _suenos.value = suenoRepository.obtenerTodos()
            _etiquetas.value = etiquetaRepository.obtenerTodas()
            _ubicaciones.value = ubicacionRepository.obtenerTodas()
        }
    }

    // Cambia el filtro de etiqueta activo en la pantalla de inicio
    fun cambiarFiltro(idEtiqueta: String?) {
        _filtroEtiqueta.value = idEtiqueta
    }

    // Devuelve los sueños filtrados según la etiqueta seleccionada
    fun suenosFiltrados(): List<Sueno> {
        val filtro = _filtroEtiqueta.value ?: return _suenos.value
        return _suenos.value.filter { filtro in it.etiquetas }
    }

    // Guarda un sueño nuevo y recarga la lista
    fun guardarSueno(sueno: Sueno) {
        viewModelScope.launch {
            suenoRepository.guardar(sueno)
            _suenos.value = suenoRepository.obtenerTodos()
        }
    }

    // Elimina un sueño y recarga la lista
    fun eliminarSueno(id: String) {
        viewModelScope.launch {
            suenoRepository.eliminar(id)
            _suenos.value = suenoRepository.obtenerTodos()
        }
    }

    // Obtiene un sueño concreto por su id
    suspend fun obtenerSueno(id: String): Sueno? {
        return suenoRepository.obtenerPorId(id)
    }

    // Crea una etiqueta personalizada nueva y recarga la lista
    fun crearEtiqueta(nombre: String) {
        viewModelScope.launch {
            etiquetaRepository.crearEtiqueta(nombre)
            _etiquetas.value = etiquetaRepository.obtenerTodas()
        }
    }

    // Guarda una nueva ubicación nombrada
    fun guardarUbicacion(ubicacion: Ubicacion) {
        viewModelScope.launch {
            ubicacionRepository.guardar(ubicacion)
            _ubicaciones.value = ubicacionRepository.obtenerTodas()
        }
    }

    // Obtiene la ubicación GPS actual del dispositivo
    fun actualizarUbicacionActual() {
        viewModelScope.launch {
            _ubicacionActual.value = locationDataSource.obtenerUbicacionActual()
        }
    }

    // Busca si la ubicación actual coincide con alguna guardada
    suspend fun encontrarUbicacionCercana(): Ubicacion? {
        val coords = _ubicacionActual.value ?: return null
        return ubicacionRepository.encontrarCercana(coords.first, coords.second)
    }
}
