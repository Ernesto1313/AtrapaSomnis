// Usa el sensor de luz del dispositivo para detectar si es de día o de noche
// y ajustar el brillo de la interfaz

package com.ernesto.atrapasomnins.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

// Niveles de luz ambiente en lux
const val UMBRAL_NOCHE = 10f    // menos de 10 lux = oscuridad
const val UMBRAL_DIA = 1000f    // más de 1000 lux = luz brillante

enum class NivelLuz { OSCURO, NORMAL, BRILLANTE }

// Composable que escucha el sensor de luz y devuelve el nivel actual
@Composable
fun rememberNivelLuz(): NivelLuz {
    val context = LocalContext.current
    var nivelLuz by remember { mutableStateOf(NivelLuz.NORMAL) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // El primer valor del array es la iluminancia en lux
                val lux = event.values[0]
                nivelLuz = when {
                    lux < UMBRAL_NOCHE -> NivelLuz.OSCURO
                    lux > UMBRAL_DIA -> NivelLuz.BRILLANTE
                    else -> NivelLuz.NORMAL
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        // Registramos el listener con frecuencia normal para no gastar batería
        sensorManager.registerListener(listener, sensorLuz, SensorManager.SENSOR_DELAY_NORMAL)

        // Cuando el composable salga de la pantalla, cancelamos el listener
        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return nivelLuz
}
