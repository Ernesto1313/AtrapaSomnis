package com.ernesto.atrapasomnins.data.model

data class ContextoNoche(
    val cansancio: Int? = null,           // 1-5
    val estres: Int? = null,              // 1-5
    val comidaPesada: Boolean? = null,
    val pantallas: Boolean? = null,
    val lectura: Boolean? = null,
    val ejercicio: Boolean? = null,
    val temperatura: String? = null,
    // "frio", "fresco", "normal", "calido", "calor"
    val etiquetasCompanero: List<String> = emptyList(),  // ids
    val etiquetasLugar: List<String> = emptyList(),      // ids
    val etiquetasSustancia: List<String> = emptyList()   // ids
)
