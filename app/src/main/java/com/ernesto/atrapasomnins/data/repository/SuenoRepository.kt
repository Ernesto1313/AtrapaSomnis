package com.ernesto.atrapasomnins.data.repository

import com.ernesto.atrapasomnins.data.model.Sueno
import com.ernesto.atrapasomnins.data.storage.JsonStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Repositorio de sueños: hace de puente entre los ViewModels y el almacenamiento.
// Todas las operaciones son suspend para ejecutarse en coroutines.
class SuenoRepository @Inject constructor(
    private val storage: JsonStorage
) {
    // Carga todos los sueños ordenados del más reciente al más antiguo
    suspend fun obtenerTodos(): List<Sueno> = withContext(Dispatchers.IO) {
        storage.cargarSuenos().sortedByDescending { it.fechaCreacion }
    }

    // Guarda un sueño nuevo añadiéndolo a la lista existente
    suspend fun guardar(sueno: Sueno) = withContext(Dispatchers.IO) {
        val lista = storage.cargarSuenos().toMutableList()
        lista.add(sueno)
        storage.guardarSuenos(lista)
    }

    // Elimina un sueño por su id
    suspend fun eliminar(id: String) = withContext(Dispatchers.IO) {
        val lista = storage.cargarSuenos().filter { it.id != id }
        storage.guardarSuenos(lista)
    }

    // Obtiene un sueño concreto por id
    suspend fun obtenerPorId(id: String): Sueno? = withContext(Dispatchers.IO) {
        storage.cargarSuenos().find { it.id == id }
    }
}
