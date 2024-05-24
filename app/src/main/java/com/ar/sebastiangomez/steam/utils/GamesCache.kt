package com.ar.sebastiangomez.steam.utils

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import com.ar.sebastiangomez.steam.model.GameCached
import com.ar.sebastiangomez.steam.ui.BookmarkActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GamesCache
{
    private val tag = "LOG-GAMES-CACHE"

    // Función para obtener la lista de IDs almacenados en caché
    fun getGamesFromCache(context: Context): MutableList<GameCached> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("bookmark_data", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("games", "[]")
        val type = object : TypeToken<MutableList<GameCached>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Función para guardar la lista de IDs en caché
    fun saveGamesToCache(context: Context, games: MutableList<GameCached>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("bookmark_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val json = Gson().toJson(games)
        editor.putString("games", json)
        editor.apply()
    }

    // Función para agregar un ID a la lista en caché
    fun addGameToCache(context: Context, game: GameCached) {
        val games = getGamesFromCache(context)
        if (!games.any { it.id == game.id }) {
            games.add(0,game)
            saveGamesToCache(context, games)
        }
        Toast.makeText(context, "Agregaste - ${game.name} - a favoritos.", Toast.LENGTH_LONG).show()
        val intent = Intent(context, BookmarkActivity::class.java)
        context.startActivity(intent)
    }

    fun removeGameToCache(context: Context, id: String, activity: String) {
        // Obtener la lista de juegos en caché
        val cachedGames = getGamesFromCache(context)

        // Encontrar el índice del juego a eliminar en la lista
        val indexToRemove = cachedGames.indexOfFirst { it.id == id }

        if (indexToRemove != -1) {
            val removedGame = cachedGames.removeAt(indexToRemove)
            saveGamesToCache(context, cachedGames)
            Log.d(tag, "Removed game from cache - ID: ${activity}, Name: ${removedGame.name}")
            Toast.makeText(context, "Eliminaste - ${removedGame.name} - de favoritos.", Toast.LENGTH_LONG).show()
            if(activity == "BookmarkActivity")
            {
                (context as BookmarkActivity).recreate()
            }
        } else {
            // El juego no se encontró en la lista en caché
            Log.d(tag, "Game not found in cache - ID: $id")
        }
    }

    // Método para obtener un juego específico por su ID
    fun getGameFromId(context: Context, id: String): GameCached? {
        val games = getGamesFromCache(context)
        return games.find { it.id == id }
    }

    // Método para verificar si un juego con un ID específico existe en la caché
    fun exists(context: Context, id: String): Boolean {
        val games = getGamesFromCache(context)
        return games.any { it.id == id }
    }

    // Método para contar todos los juegos almacenados en caché
    fun countAllGames(context: Context): Int {
        val games = getGamesFromCache(context)
        return games.size
    }
}