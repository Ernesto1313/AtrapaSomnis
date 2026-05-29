package com.ernesto.atrapasomnins.ui

// Todas las rutas de la app en un solo sitio para no tener strings sueltos
sealed class Pantalla(val ruta: String) {
    object Inicio : Pantalla("inicio")
    object RegistroSueno : Pantalla("registro_sueno")
    object DetalleSueno : Pantalla("detalle_sueno/{id}") {
        fun crearRuta(id: String) = "detalle_sueno/$id"
    }
    object Estadisticas : Pantalla("estadisticas")
    object Ajustes : Pantalla("ajustes")
}
