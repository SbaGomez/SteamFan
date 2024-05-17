package com.ar.sebastiangomez.steam.data

import com.ar.sebastiangomez.steam.model.Game
import com.ar.sebastiangomez.steam.model.GameDetail

class GamesRepository {
    suspend fun getGames() : List<Game>{
        return GamesDataSource.getGames()
    }

    suspend fun getDetails(gameId: String): GameDetail?{
        return GamesDataSource.getDetails(gameId)
    }

    suspend fun countGames() : Int{
        return GamesDataSource.countGames()
    }
}