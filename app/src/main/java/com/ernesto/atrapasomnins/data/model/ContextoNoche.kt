package com.ernesto.atrapasomnins.data.model

// Toda la info del contexto antes de dormir
data class ContextoNoche(
    // Con quién se durmió
    val companero: String? = null, // "solo", "pareja", "amigo", "otro"

    // Estado físico/mental
    val cansancio: Int? = null,    // 1-5
    val estres: Int? = null,       // 1-5

    // Hábitos esa noche
    val comidaPesada: Boolean? = null,
    val pantallas: Boolean? = null,
    val lectura: Boolean? = null,
    val ejercicio: Boolean? = null,

    // Sustancias
    val alcohol: Boolean? = null,
    val otrasSustancias: Boolean? = null, // sin especificar cuáles
    val intensidadSustancias: Int? = null, // 1-5, solo si otrasSustancias == true
    val etiquetasSustancias: List<String> = emptyList(), // ids de etiquetas
    val melatonina: Boolean? = null,
    val otrosSomniferos: String? = null, // nombre libre si quiere especificar

    // Ambiente
    val temperatura: String? = null, // "frio", "normal", "calor"

    // Ubicación
    val ubicacionId: String? = null,  // id de Ubicacion guardada
    val latitudRaw: Double? = null,   // coordenadas reales capturadas
    val longitudRaw: Double? = null
)
