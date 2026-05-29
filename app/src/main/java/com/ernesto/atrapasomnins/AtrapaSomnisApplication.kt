package com.ernesto.atrapasomnins

import android.app.Application
import com.ernesto.atrapasomnins.notificaciones.crearCanalNotificaciones
import dagger.hilt.android.HiltAndroidApp

// Clase principal de la aplicación, necesaria para que Hilt funcione
@HiltAndroidApp
class AtrapaSomnisApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        crearCanalNotificaciones(this)
    }
}
