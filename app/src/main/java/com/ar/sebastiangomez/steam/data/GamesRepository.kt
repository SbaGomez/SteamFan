package com.ar.sebastiangomez.steam.data

import com.ar.sebastiangomez.steam.model.Game

class GamesRepository {
    suspend fun getGames() : List<Game>{
        return GamesDataSource.getGames()
    }
}