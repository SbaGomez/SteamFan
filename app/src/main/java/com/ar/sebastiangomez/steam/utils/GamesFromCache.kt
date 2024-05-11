package com.ar.sebastiangomez.steam.utils

import android.content.Context
import android.content.SharedPreferences
import com.ar.sebastiangomez.steam.Game
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GamesFromCache {
    // Función para obtener la lista de IDs almacenados en caché
    fun getGamesFromCache(context: Context): MutableList<Game> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("bookmark_data", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("games", "[]")
        val type = object : TypeToken<MutableList<Game>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Función para guardar la lista de IDs en caché
    fun saveGamesToCache(context: Context, games: MutableList<Game>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("bookmark_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(games)
        editor.putString("games", json)
        editor.apply()
    }

    // Función para agregar un ID a la lista en caché
    fun addGameToCache(context: Context, game: Game) {
        val games = getGamesFromCache(context)
        if (!games.any { it.id == game.id }) {
            games.add(game)
            saveGamesToCache(context, games)
        }
    }
}