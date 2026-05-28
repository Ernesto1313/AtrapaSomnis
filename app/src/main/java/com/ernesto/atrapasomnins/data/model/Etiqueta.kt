package com.ernesto.atrapasomnins.data.model

data class Etiqueta(
    val id: String,
    val nombre: String,
    val esPersonalizada: Boolean
)

// Etiquetas predefinidas del sistema
val ETIQUETAS_PREDEFINIDAS = listOf(
    Etiqueta("pesadilla", "Pesadilla", false),
    Etiqueta("erotico", "Erótico", false),
    Etiqueta("rarote", "Rarote", false),
    Etiqueta("feliz", "Feliz", false),
    Etiqueta("traumatico", "Traumático", false),
    Etiqueta("random", "Random", false),
    Etiqueta("divertido", "Divertido", false),
    Etiqueta("nostalgico", "Nostálgico", false),
    Etiqueta("lucido", "Lúcido", false),
    Etiqueta("recurrente", "Recurrente", false),
    Etiqueta("angustiante", "Angustiante", false),
    Etiqueta("surrealista", "Surrealista", false),
    Etiqueta("romantico", "Romántico", false),
    Etiqueta("aventura", "Aventura", false),
    Etiqueta("infantil", "Infantil", false)
)
