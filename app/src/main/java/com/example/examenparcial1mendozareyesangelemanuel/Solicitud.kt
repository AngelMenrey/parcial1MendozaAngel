package com.example.examenparcial1mendozareyesangelemanuel

data class Solicitud(
    val nombre: String,
    val apellidos: String,
    val curp: String,
    val domicilio: String,
    val cantidadDeIngresos: Double,
    val tipoDeprestamo: String
) {
    fun validarIngreso(): Boolean {
        return when (tipoDeprestamo) {
            "personal" -> cantidadDeIngresos in 20000.0..40000.0
            "negocio" -> cantidadDeIngresos in 40000.0..60000.0
            "vivienda" -> cantidadDeIngresos in 15000.0..35000.0
            else -> false
        }
    }
}