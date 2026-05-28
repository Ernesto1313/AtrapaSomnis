package com.ernesto.atrapasomnins.data.storage

import android.content.Context
import com.ernesto.atrapasomnins.data.model.Etiqueta
import com.ernesto.atrapasomnins.data.model.Sueno
import com.ernesto.atrapasomnins.data.model.Ubicacion
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Gestiona la lectura y escritura de datos en ficheros JSON locales.
// Cada tipo de dato tiene su propio fichero en el almacenamiento interno.
class JsonStorage(private val context: Context) {

    private val gson = Gson()

    private val FICHERO_SUENOS = "suenos.json"
    private val FICHERO_ETIQUETAS = "etiquetas.json"
    private val FICHERO_UBICACIONES = "ubicaciones.json"

    // ── Sueños ──────────────────────────────────────────────

    fun guardarSuenos(suenos: List<Sueno>) {
        escribirFichero(FICHERO_SUENOS, gson.toJson(suenos))
    }

    fun cargarSuenos(): List<Sueno> {
        val json = leerFichero(FICHERO_SUENOS) ?: return emptyList()
        return try {
            val tipo = object : TypeToken<List<Sueno>>() {}.type
            gson.fromJson(json, tipo) ?: emptyList()
        } catch (e: Exception) {
            // Si el fichero está corrupto, devolvemos lista vacía
            emptyList()
        }
    }

    // ── Etiquetas ────────────────────────────────────────────

    fun guardarEtiquetas(etiquetas: List<Etiqueta>) {
        escribirFichero(FICHERO_ETIQUETAS, gson.toJson(etiquetas))
    }

    fun cargarEtiquetas(): List<Etiqueta> {
        val json = leerFichero(FICHERO_ETIQUETAS) ?: return emptyList()
        return try {
            val tipo = object : TypeToken<List<Etiqueta>>() {}.type
            gson.fromJson(json, tipo) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Ubicaciones ──────────────────────────────────────────

    fun guardarUbicaciones(ubicaciones: List<Ubicacion>) {
        escribirFichero(FICHERO_UBICACIONES, gson.toJson(ubicaciones))
    }

    fun cargarUbicaciones(): List<Ubicacion> {
        val json = leerFichero(FICHERO_UBICACIONES) ?: return emptyList()
        return try {
            val tipo = object : TypeToken<List<Ubicacion>>() {}.type
            gson.fromJson(json, tipo) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Helpers privados ─────────────────────────────────────

    private fun escribirFichero(nombre: String, contenido: String) {
        // Abre o crea el fichero en modo privado y escribe el JSON
        context.openFileOutput(nombre, Context.MODE_PRIVATE).use { stream ->
            stream.write(contenido.toByteArray())
        }
    }

    private fun leerFichero(nombre: String): String? {
        return try {
            context.openFileInput(nombre).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            // Si el fichero no existe todavía, devolvemos null
            null
        }
    }
}
