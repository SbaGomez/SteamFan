package com.ar.sebastiangomez.steam.data

import android.util.Log
import com.ar.sebastiangomez.steam.model.Game
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val tag = "LOG-API"
class GamesDataSource {
    companion object{
        private const val API_BASE_URL = "https://api.steampowered.com/"

        private val apiGames : SteamApiService = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(SteamApiService::class.java)
        suspend fun getGames() : List<Game>{
            Log.d(tag, "Games DataSource Get")

            val response = apiGames.getAppList()

            return response.appList.apps
                .map { steamApp -> Game(steamApp.id, steamApp.name) }
                .filter { game -> game.name.isNotEmpty() }
        }
    }
}