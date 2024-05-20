package com.ar.sebastiangomez.steam.data

import com.ar.sebastiangomez.steam.model.Dolar
import retrofit2.http.GET

interface DolarApiService {
    @GET("v1/dolares/tarjeta")
    suspend fun getDolarTarjeta(): Dolar

}