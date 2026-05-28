package com.ernesto.atrapasomnins.data.model

data class Ubicacion(
    val id: String,
    val nombre: String,
    val latitud: Double,
    val longitud: Double,
    val radio: Double = 300.0
)
