package com.ar.sebastiangomez.steam.data

import android.util.Log
import com.ar.sebastiangomez.steam.model.Dolar
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val tag = "LOG-API"
class DolarDataSource {
    companion object{
        private const val API_BASE_URL = "https://dolarapi.com/"

        private val apiDolar : DolarApiService = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(DolarApiService::class.java)

        suspend fun getDolarTarjeta(): Dolar? {
            Log.d(tag, "Dolar DataSource Get")

            return try {
                val response = apiDolar.getDolarTarjeta()
                Log.d(tag, "Dolar Tarjeta: $response")
                response
            } catch (e: Exception) {
                Log.e(tag, "ERROR: Failed to fetch dolar tarjeta: ${e.message}")
                null
            }
        }
    }
}