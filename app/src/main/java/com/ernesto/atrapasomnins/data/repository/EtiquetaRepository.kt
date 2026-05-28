package com.ernesto.atrapasomnins.data.repository

import com.ernesto.atrapasomnins.data.model.ETIQUETAS_PREDEFINIDAS
import com.ernesto.atrapasomnins.data.model.Etiqueta
import com.ernesto.atrapasomnins.data.storage.JsonStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Repositorio de etiquetas: combina las predefinidas con las del usuario
class EtiquetaRepository @Inject constructor(
    private val storage: JsonStorage
) {
    // Devuelve todas las etiquetas: primero las predefinidas, luego las del usuario
    suspend fun obtenerTodas(): List<Etiqueta> = withContext(Dispatchers.IO) {
        val personalizadas = storage.cargarEtiquetas()
        ETIQUETAS_PREDEFINIDAS + personalizadas
    }

    // Crea una etiqueta nueva personalizada
    suspend fun crearEtiqueta(nombre: String): Etiqueta = withContext(Dispatchers.IO) {
        val nueva = Etiqueta(
            id = java.util.UUID.randomUUID().toString(),
            nombre = nombre,
            esPersonalizada = true
        )
        val lista = storage.cargarEtiquetas().toMutableList()
        lista.add(nueva)
        storage.guardarEtiquetas(lista)
        nueva
    }

    // Elimina una etiqueta personalizada (las predefinidas no se pueden borrar)
    suspend fun eliminar(id: String) = withContext(Dispatchers.IO) {
        val lista = storage.cargarEtiquetas().filter { it.id != id }
        storage.guardarEtiquetas(lista)
    }
}
