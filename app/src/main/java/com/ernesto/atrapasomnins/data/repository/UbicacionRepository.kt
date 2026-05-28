package com.ernesto.atrapasomnins.data.repository

import com.ernesto.atrapasomnins.data.model.Ubicacion
import com.ernesto.atrapasomnins.data.storage.JsonStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Repositorio de ubicaciones guardadas
class UbicacionRepository @Inject constructor(
    private val storage: JsonStorage
) {
    suspend fun obtenerTodas(): List<Ubicacion> = withContext(Dispatchers.IO) {
        storage.cargarUbicaciones()
    }

    // Guarda una nueva ubicación nombrada
    suspend fun guardar(ubicacion: Ubicacion) = withContext(Dispatchers.IO) {
        val lista = storage.cargarUbicaciones().toMutableList()
        lista.add(ubicacion)
        storage.guardarUbicaciones(lista)
    }

    // Busca si las coordenadas actuales coinciden con alguna ubicación guardada.
    // Devuelve la primera que esté dentro del radio.
    suspend fun encontrarCercana(lat: Double, lon: Double): Ubicacion? =
        withContext(Dispatchers.IO) {
            storage.cargarUbicaciones().find { ubi ->
                val resultados = FloatArray(1)
                android.location.Location.distanceBetween(
                    lat, lon, ubi.latitud, ubi.longitud, resultados
                )
                resultados[0] <= ubi.radio
            }
        }
}
