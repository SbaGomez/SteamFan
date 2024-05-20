package com.ar.sebastiangomez.steam.model

import java.util.Date

data class Dolar (
    val moneda: String,
    val casa: String,
    val nombre: String,
    val compra: Double,
    val venta: Double,
    val fechaActualizacion: Date
)