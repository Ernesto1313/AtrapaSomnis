package com.ernesto.atrapasomnins.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

// Fuente de datos de ubicación: obtiene la posición GPS actual del dispositivo
class LocationDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val clienteUbicacion = LocationServices.getFusedLocationProviderClient(context)

    // Devuelve la última ubicación conocida o null si no hay permiso/ubicación
    suspend fun obtenerUbicacionActual(): Pair<Double, Double>? =
        withContext(Dispatchers.IO) {
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) return@withContext null

                // Usamos await() de las coroutines de Google Play Services
                val ubicacion = clienteUbicacion.lastLocation.await()
                ubicacion?.let { Pair(it.latitude, it.longitude) }
            } catch (e: Exception) {
                null
            }
        }
}
