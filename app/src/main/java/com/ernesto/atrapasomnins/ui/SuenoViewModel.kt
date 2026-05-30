package com.ernesto.atrapasomnins.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ernesto.atrapasomnins.data.model.Etiqueta
import com.ernesto.atrapasomnins.data.model.Sueno
import com.ernesto.atrapasomnins.data.repository.EtiquetaRepository
import com.ernesto.atrapasomnins.data.repository.SuenoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuenoViewModel @Inject constructor(
    private val suenoRepository: SuenoRepository,
    private val etiquetaRepository: EtiquetaRepository
) : ViewModel() {

    private val _suenos = MutableStateFlow<List<Sueno>>(emptyList())
    val suenos: StateFlow<List<Sueno>> = _suenos.asStateFlow()

    private val _etiquetas = MutableStateFlow<List<Etiqueta>>(emptyList())
    val etiquetas: StateFlow<List<Etiqueta>> = _etiquetas.asStateFlow()

    private val _filtroEtiqueta = MutableStateFlow<String?>(null)
    val filtroEtiqueta: StateFlow<String?> = _filtroEtiqueta.asStateFlow()

    init {
        cargarDatos()
    }

    fun cargarDatos() {
        viewModelScope.launch {
            _suenos.value = suenoRepository.obtenerTodos()
            _etiquetas.value = etiquetaRepository.obtenerTodas()
        }
    }

    fun cambiarFiltro(idEtiqueta: String?) {
        _filtroEtiqueta.value = idEtiqueta
    }

    fun suenosFiltrados(): List<Sueno> {
        val filtro = _filtroEtiqueta.value ?: return _suenos.value
        return _suenos.value.filter { filtro in it.etiquetas }
    }

    fun guardarSueno(sueno: Sueno) {
        viewModelScope.launch {
            suenoRepository.guardar(sueno)
            _suenos.value = suenoRepository.obtenerTodos()
        }
    }

    fun eliminarSueno(id: String) {
        viewModelScope.launch {
            suenoRepository.eliminar(id)
            _suenos.value = suenoRepository.obtenerTodos()
        }
    }

    suspend fun obtenerSueno(id: String): Sueno? {
        return suenoRepository.obtenerPorId(id)
    }

    fun crearEtiqueta(nombre: String) {
        viewModelScope.launch {
            etiquetaRepository.crearEtiqueta(nombre)
            _etiquetas.value = etiquetaRepository.obtenerTodas()
        }
    }

    fun eliminarEtiqueta(id: String) {
        viewModelScope.launch {
            etiquetaRepository.eliminar(id)
            _etiquetas.value = etiquetaRepository.obtenerTodas()
        }
    }
}
