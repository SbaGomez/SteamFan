package com.ar.sebastiangomez.steam.data

import android.content.Context
import com.ar.sebastiangomez.steam.model.Game
import com.ar.sebastiangomez.steam.model.GameCached
import com.ar.sebastiangomez.steam.model.GameDetail

class GamesRepository {
    suspend fun getGames() : List<Game>{
        return GamesDataSource.getGames()
    }

    suspend fun getDetails(gameId: String, context: Context): GameDetail?{
        return GamesDataSource.getDetails(gameId, context)
    }

    suspend fun deleteRoom(gameId: String, context: Context){
        return GamesDataSource.deleteRoom(gameId, context)
    }

    suspend fun deleteAllRoom(context: Context){
        return GamesDataSource.deleteAllRoom(context)
    }

    suspend fun isGameSuccess(gameId: String): Boolean? {
        return GamesDataSource.isSuccess(gameId)
    }

    suspend fun getImage(gameId: String): String?{
        return GamesDataSource.getImage(gameId)
    }

    suspend fun countGames() : Int{
        return GamesDataSource.countGames()
    }

    suspend fun saveGameFirestore(context: Context, gameCached: GameCached): Boolean? {
        return GamesDataSource.saveGameFirestore(context, gameCached)
    }

    suspend fun getGamesFirestore(): List<GameCached> {
        return GamesDataSource.getGamesFirestore()
    }

    fun removeGameFirestore(context: Context, gameId: String, gameName: String, activity: String? = null): Boolean {
        return GamesDataSource.removeGameFirestore(context, gameId, gameName, activity)
    }

    suspend fun existsGameFirestore(gameId: String): Boolean {
        return GamesDataSource.existsGameFirestore(gameId)
    }

    suspend fun countAllGamesFirestore(): Int {
        return GamesDataSource.countAllGamesFirestore()
    }
}