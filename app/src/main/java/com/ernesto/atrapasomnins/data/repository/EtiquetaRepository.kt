package com.ernesto.atrapasomnins.data.repository

import com.ernesto.atrapasomnins.data.model.CategoriaEtiqueta
import com.ernesto.atrapasomnins.data.model.Etiqueta
import com.ernesto.atrapasomnins.data.model.ETIQUETAS_PREDEFINIDAS
import com.ernesto.atrapasomnins.data.storage.JsonStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class EtiquetaRepository @Inject constructor(
    private val storage: JsonStorage
) {
    // Devuelve todas las etiquetas de una categoría concreta
    suspend fun obtenerPorCategoria(categoria: CategoriaEtiqueta): List<Etiqueta> =
        withContext(Dispatchers.IO) {
            val personalizadas = storage.cargarEtiquetas()
                .filter { it.categoria == categoria }
            ETIQUETAS_PREDEFINIDAS.filter { it.categoria == categoria } + personalizadas
        }

    // Devuelve todas sin distinción
    suspend fun obtenerTodas(): List<Etiqueta> = withContext(Dispatchers.IO) {
        ETIQUETAS_PREDEFINIDAS + storage.cargarEtiquetas()
    }

    // Crea una etiqueta personalizada en la categoría indicada
    suspend fun crearEtiqueta(
        nombre: String,
        categoria: CategoriaEtiqueta = CategoriaEtiqueta.PERSONALIZADA
    ): Etiqueta = withContext(Dispatchers.IO) {
        val nueva = Etiqueta(
            id = UUID.randomUUID().toString(),
            nombre = nombre,
            categoria = categoria,
            esPersonalizada = true
        )
        val lista = storage.cargarEtiquetas().toMutableList()
        lista.add(nueva)
        storage.guardarEtiquetas(lista)
        nueva
    }

    suspend fun eliminar(id: String) = withContext(Dispatchers.IO) {
        val lista = storage.cargarEtiquetas().filter { it.id != id }
        storage.guardarEtiquetas(lista)
    }
}
