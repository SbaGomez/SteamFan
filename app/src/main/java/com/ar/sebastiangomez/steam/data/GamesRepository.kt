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

    suspend fun isGameSuccess(gameId: String): Boolean? {
        return GamesDataSource.isSuccess(gameId)
    }

    suspend fun getImage(gameId: String): String?{
        return GamesDataSource.getImage(gameId)
    }

    suspend fun countGames() : Int{
        return GamesDataSource.countGames()
    }

    suspend fun saveGameCached(context: Context, gameCached: GameCached): Boolean? {
        return GamesDataSource.saveGameCached(context, gameCached)
    }

    suspend fun getUserGameCached(): List<GameCached> {
        return GamesDataSource.getUserGameCached()
    }

    fun removeGameCached(context: Context, gameId: String, gameName: String, activity: String? = null): Boolean {
        return GamesDataSource.removeGameCached(context, gameId, gameName, activity)
    }

    suspend fun exists(gameId: String): Boolean {
        return GamesDataSource.exists(gameId)
    }

    suspend fun countAllGames(): Int {
        return GamesDataSource.countAllGames()
    }
}