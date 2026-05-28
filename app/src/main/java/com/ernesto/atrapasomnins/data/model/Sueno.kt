package com.ernesto.atrapasomnins.data.model

data class Sueno(
    val id: String,
    val fechaCreacion: Long, // timestamp millis al despertar

    // ¿Ha soñado?
    val estado: EstadoSueno,

    // Solo si estado == RECORDADO
    val titulo: String? = null,
    val descripcion: String? = null,
    val etiquetas: List<String> = emptyList(), // ids de etiquetas
    val intensidad: Int? = null,               // 1-5, qué tan vívido
    val lucido: Boolean? = null,

    // Qué le despertó (selección múltiple)
    val causasDespertar: List<String> = emptyList(),
    // valores: "alarma", "luz", "ruido", "sueno_propio", "alguien",
    //          "nada", "llamada", "timbre", "sed_calor_frio", "solo"

    // Contexto de la noche anterior
    val contextoNoche: ContextoNoche? = null
)

enum class EstadoSueno {
    NO_HE_SONADO,
    NO_RECUERDO,
    RECORDADO
}
