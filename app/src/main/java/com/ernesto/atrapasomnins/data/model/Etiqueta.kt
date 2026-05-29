package com.ernesto.atrapasomnins.data.model

data class Etiqueta(
    val id: String,
    val nombre: String,
    val categoria: CategoriaEtiqueta,
    val esPersonalizada: Boolean
)

enum class CategoriaEtiqueta {
    SUENO,        // etiquetas del sueño (pesadilla, feliz...)
    COMPANERO,    // con quién dormiste
    LUGAR,        // dónde dormiste
    SUSTANCIA,    // sustancias tomadas
    PERSONALIZADA // creadas por el usuario
}

// Etiquetas predefinidas del sueño
val ETIQUETAS_SUENO = listOf(
    Etiqueta("pesadilla", "Pesadilla", CategoriaEtiqueta.SUENO, false),
    Etiqueta("erotico", "Erótico", CategoriaEtiqueta.SUENO, false),
    Etiqueta("raro", "Raro", CategoriaEtiqueta.SUENO, false),
    Etiqueta("feliz", "Feliz", CategoriaEtiqueta.SUENO, false),
    Etiqueta("traumatico", "Traumático", CategoriaEtiqueta.SUENO, false),
    Etiqueta("random", "Random", CategoriaEtiqueta.SUENO, false),
    Etiqueta("divertido", "Divertido", CategoriaEtiqueta.SUENO, false),
    Etiqueta("nostalgico", "Nostálgico", CategoriaEtiqueta.SUENO, false),
    Etiqueta("lucido", "Lúcido", CategoriaEtiqueta.SUENO, false),
    Etiqueta("recurrente", "Recurrente", CategoriaEtiqueta.SUENO, false),
    Etiqueta("angustiante", "Angustiante", CategoriaEtiqueta.SUENO, false),
    Etiqueta("surrealista", "Surrealista", CategoriaEtiqueta.SUENO, false),
    Etiqueta("romantico", "Romántico", CategoriaEtiqueta.SUENO, false),
    Etiqueta("aventura", "Aventura", CategoriaEtiqueta.SUENO, false),
    Etiqueta("infantil", "Infantil", CategoriaEtiqueta.SUENO, false)
)

// Etiquetas predefinidas de compañero
val ETIQUETAS_COMPANERO = listOf(
    Etiqueta("solo", "Solo", CategoriaEtiqueta.COMPANERO, false),
    Etiqueta("pareja", "Pareja", CategoriaEtiqueta.COMPANERO, false),
    Etiqueta("amigo", "Amigo/a", CategoriaEtiqueta.COMPANERO, false)
)

// Etiquetas predefinidas de lugar
val ETIQUETAS_LUGAR = listOf(
    Etiqueta("mi_cama", "Mi cama", CategoriaEtiqueta.LUGAR, false),
    Etiqueta("sofa_amigo", "Sofá de un amigo", CategoriaEtiqueta.LUGAR, false),
    Etiqueta("casa_padres", "Casa de mis padres", CategoriaEtiqueta.LUGAR, false)
)

// Etiquetas predefinidas de sustancias
val ETIQUETAS_SUSTANCIA = listOf(
    Etiqueta("alcohol", "Alcohol", CategoriaEtiqueta.SUSTANCIA, false),
    Etiqueta("melatonina", "Melatonina", CategoriaEtiqueta.SUSTANCIA, false),
    Etiqueta("tabaco", "Tabaco", CategoriaEtiqueta.SUSTANCIA, false),
    Etiqueta("cannabis", "Cannabis", CategoriaEtiqueta.SUSTANCIA, false),
    Etiqueta("somniferos", "Somníferos", CategoriaEtiqueta.SUSTANCIA, false)
)

// Todas las predefinidas juntas
val ETIQUETAS_PREDEFINIDAS = ETIQUETAS_SUENO + ETIQUETAS_COMPANERO +
    ETIQUETAS_LUGAR + ETIQUETAS_SUSTANCIA
